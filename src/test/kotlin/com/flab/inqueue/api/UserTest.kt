package com.flab.inqueue.api

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.REST_DOCS_DOCUMENT_IDENTIFIER
import com.flab.inqueue.createEventRequest
import com.flab.inqueue.domain.auth.dto.TokenResponse
import com.flab.inqueue.domain.event.dto.EventInformation
import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.entity.MemberKey
import com.flab.inqueue.domain.member.repository.MemberRepository
import com.flab.inqueue.domain.member.utils.memberkeygenrator.MemberKeyGenerator
import com.flab.inqueue.security.hmacsinature.createHmacAuthorizationHeader
import com.flab.inqueue.security.hmacsinature.createHmacSignature
import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import io.restassured.response.ExtractableResponse
import io.restassured.response.Response
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation
import org.springframework.restdocs.restassured.RestDocumentationFilter
import org.springframework.restdocs.snippet.Snippet
import java.time.LocalDateTime


class UserTest : AcceptanceTest() {
    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var eventRepository: EventRepository
    @Autowired
    lateinit var encryptionUtil: EncryptionUtil
    @Autowired
    lateinit var memberKeyGenerator: MemberKeyGenerator



    lateinit var testMember: Member
    lateinit var event : Event
    lateinit var notEncryptedUserMemberKey: MemberKey
    lateinit var hmacSignaturePayload: String
    var tokenResponse : TokenResponse? = null

    companion object {
        const val ISSUE_TOKEN_URL = "/server/v1/auth/token"
    }

    @BeforeEach
    fun setUp(@LocalServerPort port: Int) {
        hmacSignaturePayload = "http://localhost:${port}" + ISSUE_TOKEN_URL
        notEncryptedUserMemberKey = memberKeyGenerator.generate()
        testMember = Member(
            "TEST_MEMBER",
            MemberKey(notEncryptedUserMemberKey.clientId, notEncryptedUserMemberKey.clientSecret)
        )
        testMember.encryptMemberKey(encryptionUtil)
        memberRepository.save(testMember)

        event = createEventRequest(
            null,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            10L,
            1L,
            EventInformation("testEvent",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                "test description",
                "test place",
                100L,
                "TEST CONCERT"),
            "https://test"
        ).toEntity()

        eventRepository.save(event)
    }

    @Test
    @DisplayName("토큰 발급 api")
    fun issueToken() {
        val response = givenWithDocument.log().all()
            .filter(IssueTokenDocument.FILTER)
            .header(
                HttpHeaders.AUTHORIZATION,
                createHmacAuthorizationHeader(
                    notEncryptedUserMemberKey.clientId,
                    createHmacSignature(hmacSignaturePayload, notEncryptedUserMemberKey.clientSecret)
                )
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post(ISSUE_TOKEN_URL).
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .body()

        tokenResponse = response.`as`(TokenResponse::class.java)

        val body = response.jsonPath()
        assertThat(body.get<Any>("userId")).isNotNull
        assertThat(body.get<Any>("token.accessToken")).isNotNull
        val tokenExpiration = LocalDateTime.parse(body.get<String>("token.expiration").toString())
        assertThat(tokenExpiration).isAfter(LocalDateTime.now())

    }

    @Test
    @DisplayName("사용자 작업열 최초 진입 api")
    fun enterJobQueue(restDocumentation: RestDocumentationContextProvider, @LocalServerPort port: Int) {
        val response = extractableResponse(restDocumentation, port)

        val body = response.body().jsonPath()

        assertThat("ENTER").isEqualTo(body.get("status"))
        assertThat(body.get<Any>("expectedInfo")).isNull()
    }

    @Test
    @DisplayName("사용자 대기열 최초 진입 api")
    fun enterWaitQueue(restDocumentation: RestDocumentationContextProvider, @LocalServerPort port: Int) {
        extractableResponse(restDocumentation,port)
        super.setUpRequestSpecification(restDocumentation, port)
        val response = givenWithDocument.log().all()
            .filter(EnterWaitQueueDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenResponse?.token?.accessToken}")
            .pathParam("eventId", event.eventId)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
            `when`()
            .post("/client/v1/events/{eventId}/enter").
            then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()

        assertThat("WAIT").isEqualTo(body.get("status"))
        assertThat(body.get<Any>("expectedInfo.time")).isNotNull
        assertThat(body.get<Any>("expectedInfo.order")).isEqualTo(1)
    }

    private fun extractableResponse(
        restDocumentation: RestDocumentationContextProvider,
        port: Int,
    ): ExtractableResponse<Response> {
        issueToken()
        super.setUpRequestSpecification(restDocumentation, port)
        val response = givenWithDocument.log().all()
            .filter(EnterWaitQueueDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenResponse?.token?.accessToken}")
            .pathParam("eventId", event.eventId)
            .contentType(MediaType.APPLICATION_JSON_VALUE).`when`()
            .post("/client/v1/events/{eventId}/enter").then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
        return response
    }

    @Test
    @DisplayName("사용자 대기열 조회 api")
    fun retrieveWaitQueue() {
        val eventId = "testEvent1"

        val response = givenWithDocument.log().all()
            .filter(RetrieveWaitQueueDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "AccessToken")
            .pathParam("eventId", eventId)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .get("v1/events/{eventId}").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()

        assertThat(listOf("WAIT","ENTER")).contains(body.get("status"))
        assertThat(body.get<Any>("expectedInfo.time")).isNotNull
        assertThat(body.get<Any>("expectedInfo.order")).isEqualTo(1)
    }
}

object IssueTokenDocument {
    val FILTER: RestDocumentationFilter = RestAssuredRestDocumentation.document(
        REST_DOCS_DOCUMENT_IDENTIFIER,
        headerFiledSnippet(),
        responseFieldsSnippet()
    )

    private fun headerFiledSnippet(): Snippet {
        return requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION)
                .description("client_id:(request_url을 client_secret 사용하여 Hmac 암호화)"),
            headerWithName(HttpHeaders.CONTENT_TYPE).description("요청-Type"),
        )
    }

    private fun responseFieldsSnippet(): Snippet {
        return responseFields(
            fieldWithPath("userId").type(JsonFieldType.STRING).description("대기열 유저 식별자"),
            fieldWithPath("token.accessToken").type(JsonFieldType.STRING).description("JWT 토큰"),
            fieldWithPath("token.expiration").type(JsonFieldType.STRING).description("토큰 만료 일시")
        )
    }
}

object EnterWaitQueueDocument {
    val FILTER: RestDocumentationFilter = RestAssuredRestDocumentation.document(
        REST_DOCS_DOCUMENT_IDENTIFIER,
        headerFiledSnippet(),
        pathParametersSnippet(),
        responseFieldsSnippet()
    )

    private fun headerFiledSnippet(): Snippet {
        return requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("JWT TOKEN"),
            headerWithName(HttpHeaders.CONTENT_TYPE).description("요청-Type"),
        )
    }

    private fun pathParametersSnippet(): Snippet {
        return pathParameters(
            parameterWithName("eventId").description("이벤트 식별자"),
        )
    }

    private fun responseFieldsSnippet(): Snippet {
        return responseFields(
            fieldWithPath("status").type(JsonFieldType.STRING).description("대기 상태(ENTER, WAIT)"),
            fieldWithPath("expectedInfo").type(JsonFieldType.OBJECT).description("대기 정보").optional(),
            fieldWithPath("expectedInfo.time").type(JsonFieldType.NUMBER).description("대기 시간(초)").optional(),
            fieldWithPath("expectedInfo.order").type(JsonFieldType.NUMBER).description("대기 순번").optional(),
        )
    }
}

object RetrieveWaitQueueDocument {
    val FILTER: RestDocumentationFilter = RestAssuredRestDocumentation.document(
        REST_DOCS_DOCUMENT_IDENTIFIER,
        headerFiledSnippet(),
        pathParametersSnippet(),
        responseFieldsSnippet()
    )

    private fun headerFiledSnippet(): Snippet {
        return requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION)
                .description("X-Client-Id:(StringToSign를 ClientSecret으로 Hmac 암호화)"),
            headerWithName("X-Client-Id").description("고객사 식별자"),
            headerWithName(HttpHeaders.CONTENT_TYPE).description("요청-Type"),
        )
    }

    private fun pathParametersSnippet(): Snippet {
        return pathParameters(
            parameterWithName("eventId").description("이벤트 식별자"),
        )
    }

    private fun responseFieldsSnippet(): Snippet {
        return responseFields(
            fieldWithPath("status").type(JsonFieldType.STRING).description("대기 상태(ENTER, WAIT)"),
            fieldWithPath("expectedInfo.time").type(JsonFieldType.STRING).description("대기 시간"),
            fieldWithPath("expectedInfo.order").type(JsonFieldType.NUMBER).description("대기 순번"),
        )
    }
}
