package com.flab.inqueue.api

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.REST_DOCS_DOCUMENT_IDENTIFIER
import com.flab.inqueue.domain.event.entity.EventInformation
import com.flab.inqueue.domain.member.dto.MemberSignUpRequest
import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.entity.MemberKey
import com.flab.inqueue.domain.member.repository.MemberRepository
import com.flab.inqueue.domain.member.utils.memberkeygenrator.MemberKeyGenerator
import com.flab.inqueue.fixture.createEventRequest
import com.flab.inqueue.security.hmacsinature.createHmacAuthorizationHeader
import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document
import org.springframework.restdocs.restassured.RestDocumentationFilter
import org.springframework.restdocs.snippet.Attributes.*
import org.springframework.restdocs.snippet.Snippet
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

class ClientTest : AcceptanceTest() {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var encryptionUtil: EncryptionUtil

    @Autowired
    lateinit var memberKeyGenerator: MemberKeyGenerator


    var port: Int = 0
    lateinit var member: Member
    lateinit var notEncryptedUserMemberKey: MemberKey

    @BeforeEach
    @Transactional
    fun setUp(@LocalServerPort localPort: Int) {
        port = localPort
        notEncryptedUserMemberKey = memberKeyGenerator.generate()
        member = Member(
            "TEST_MEMBER",
            key = notEncryptedUserMemberKey.encrypt(encryptionUtil)
        )
        memberRepository.save(member)
    }

    @Test
    @DisplayName("Member 회원가입")
    fun signUpMember() {
        val request = MemberSignUpRequest("testName")
        val response = givenWithDocument.log().all()
            .filter(SignUpMemberDocument.FILTER)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request).
        `when`()
            .post("/server/v1/members").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()

        assertThat(body.get<String>("name")).isEqualTo("testName")
        assertThat(body.get<String>("key.clientId")).isNotEmpty
        assertThat(body.get<String>("key.clientSecret")).isNotEmpty
    }

    @Test
    @DisplayName("행사 도메인 CRUD")
    fun createEvent() {
        val hmacSignaturePayload =
            "http://localhost:${port}/server/v1/events"

        val eventRequest = createEventRequest(
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            1L,
            10L,
            EventInformation(
                "testEvent",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                "test description",
                "test place",
                100L,
                "TEST CONCERT"
            ),
            "https://test"
        )

        val response = givenWithDocument.log().all()
            .filter(CreateEventDocument.FILTER)
            .header(
                HttpHeaders.AUTHORIZATION,
                createHmacAuthorizationHeader(
                    member.key.clientId, notEncryptedUserMemberKey.clientSecret, hmacSignaturePayload
                )
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(eventRequest).
        `when`()
            .post("/server/v1/events").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()

        assertThat(body.get<String>("eventId")).isNotNull
        assertThat(body.get<String>("eventId")).isNotEmpty
    }
}

object CreateEventDocument {

    val FILTER: RestDocumentationFilter = document(
        REST_DOCS_DOCUMENT_IDENTIFIER,
        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
        headerFiledSnippet(),
        requestFieldsSnippet(),
        responseFieldsSnippet(),
    )

    private fun headerFiledSnippet() : Snippet {
        return requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("X-Client-Id:(StringToSign를 ClientSecret으로 Hmac 암호화)"),
            headerWithName(HttpHeaders.CONTENT_TYPE).description("요청-Type")
        )
    }

    private fun requestFieldsSnippet(): Snippet {
        return requestFields(
            fieldWithPath("eventId").type(JsonFieldType.STRING).description("이벤트 식별자").optional(),
            fieldWithPath("eventInformation.name").type(JsonFieldType.STRING).description("이름").optional(),
            fieldWithPath("eventInformation.description").type(JsonFieldType.STRING).description("설명").optional(),
            fieldWithPath("eventInformation.place").type(JsonFieldType.STRING).description("장소").optional(),
            fieldWithPath("eventInformation.startTime").type(JsonFieldType.ARRAY).description("행사 시작 시간").optional(),
            fieldWithPath("eventInformation.endTime").type(JsonFieldType.ARRAY).description("행사 마침 시간").optional(),
            fieldWithPath("eventInformation.personnel").type(JsonFieldType.NUMBER).description("인원").optional(),
            fieldWithPath("waitQueueStartTime").type(JsonFieldType.ARRAY).description("대기 큐 시작 시간"),
            fieldWithPath("waitQueueEndTime").type(JsonFieldType.ARRAY).description("대기 큐 마침 시간"),
            fieldWithPath("eventInformation.type").type(JsonFieldType.STRING).description("행사 종류").optional(),
            fieldWithPath("jobQueueSize").type(JsonFieldType.NUMBER).description("작업 큐 크기"),
            fieldWithPath("jobQueueLimitTime").type(JsonFieldType.NUMBER).description("작업 큐 제한 시간"),
            fieldWithPath("redirectUrl").type(JsonFieldType.STRING).description("redirectUrl").optional(),
        )
    }

    private fun responseFieldsSnippet(): Snippet {
        return responseFields(
            fieldWithPath("eventId").type(JsonFieldType.STRING).description("이벤트 식별자"),
        )
    }
}

object SignUpMemberDocument {

    val FILTER: RestDocumentationFilter = document(
        REST_DOCS_DOCUMENT_IDENTIFIER,
        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
        headerFiledSnippet(),
        requestFieldsSnippet(),
        responseFieldsSnippet(),
    )

    private fun headerFiledSnippet() : Snippet {
        return requestHeaders(
            headerWithName(HttpHeaders.CONTENT_TYPE).description("요청-Type")
        )
    }

    private fun requestFieldsSnippet(): Snippet {
        return requestFields(
            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
            fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호").optional(),
        )
    }

    private fun responseFieldsSnippet(): Snippet {
        return responseFields(
            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
            fieldWithPath("key.clientId").type(JsonFieldType.STRING).description("Client-Id"),
            fieldWithPath("key.clientSecret").type(JsonFieldType.STRING).description("Client-Secret"),
        )
    }
}
