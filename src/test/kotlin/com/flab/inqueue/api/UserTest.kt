package com.flab.inqueue.api

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.REST_DOCS_DOCUMENT_IDENTIFIER
import com.flab.inqueue.domain.event.entity.EventInformation
import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.entity.MemberKey
import com.flab.inqueue.domain.member.repository.MemberRepository
import com.flab.inqueue.domain.member.utils.memberkeygenrator.MemberKeyGenerator
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.JobRedisRepository
import com.flab.inqueue.domain.queue.repository.WaitQueueRedisRepository
import com.flab.inqueue.fixture.createEventRequest
import com.flab.inqueue.security.hmacsinature.createHmacAuthorizationHeader
import com.flab.inqueue.security.hmacsinature.createHmacSignature
import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import com.flab.inqueue.security.jwt.utils.JwtUtils
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.*
import org.hamcrest.Matchers.notNullValue
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
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation
import org.springframework.restdocs.restassured.RestDocumentationFilter
import org.springframework.restdocs.snippet.Snippet
import java.time.LocalDateTime
import java.util.*

class UserTest : AcceptanceTest() {
    @Autowired
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var eventRepository: EventRepository
    @Autowired
    lateinit var encryptionUtil: EncryptionUtil
    @Autowired
    lateinit var memberKeyGenerator: MemberKeyGenerator
    @Autowired
    lateinit var jwtUtils: JwtUtils
    @Autowired
    lateinit var waitQueueRedisRepository: WaitQueueRedisRepository
    @Autowired
    lateinit var jobRedisRepository: JobRedisRepository

    lateinit var member: Member
    lateinit var event : Event
    lateinit var notEncryptedUserMemberKey: MemberKey
    lateinit var hmacSignaturePayload: String

    companion object {
        const val ISSUE_TOKEN_URL = "/server/v1/auth/token"
    }

    @BeforeEach
    @Transactional
    fun setUp(@LocalServerPort port: Int) {
        hmacSignaturePayload = "http://localhost:${port}" + ISSUE_TOKEN_URL
        notEncryptedUserMemberKey = memberKeyGenerator.generate()
        member = Member(
            name = "TEST_MEMBER",
            key = notEncryptedUserMemberKey.encrypt(encryptionUtil)
        )
        memberRepository.save(member)

        event = createEventRequest(
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
        ).toEntity(UUID.randomUUID().toString(), member)

        eventRepository.save(event)
    }

    @Test
    @DisplayName("토큰 발급 api")
    fun issueToken() {
        givenWithDocument.log().all()
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
            .body("userId", notNullValue())
            .body("token.accessToken", notNullValue())
            .body("token.expiration", notNullValue())
    }

    @Test
    @DisplayName("사용자 작업열 진입불가 및 사용자 대기열 진입")
    fun enterWaitQueue() {

        val userId = UUID.randomUUID().toString()
        val job = Job(event.eventId, userId, JobStatus.ENTER, queueLimitTime = event.jobQueueLimitTime, jobQueueSize = event.jobQueueSize)
        jobRedisRepository.register(job)

        val accessToken = jwtUtils.create(event.eventId, UUID.randomUUID().toString()).accessToken

        val response = givenWithDocument.log().all()
            .filter(EnterWaitQueueDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .pathParam("eventId", event.eventId)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post("/client/v1/events/{eventId}/enter").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()

        assertThat(body.get<String>("status")).isEqualTo("WAIT")
        assertThat(body.get<Any>("expectedInfo.second")).isNotNull
        assertThat(body.get<Any>("expectedInfo.order")).isNotNull
    }
    @Test
    @DisplayName("사용자 작업열 진입")
    fun enterJobQueue() {

        val accessToken = jwtUtils.create(event.eventId, UUID.randomUUID().toString()).accessToken

        val response = givenWithDocument.log().all()
            .filter(EnterWaitQueueDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .pathParam("eventId", event.eventId)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
            `when`()
            .post("/client/v1/events/{eventId}/enter").
            then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()


        assertThat(body.get<String>("status")).isEqualTo("ENTER")
        assertThat(body.get<Any>("expectedInfo")).isNull()
        assertThat(body.get<Any>("expectedInfo.second")).isNull()
        assertThat(body.get<Any>("expectedInfo.order")).isNull()
    }

    @Test
    @DisplayName("사용자 대기열 조회 api")
    fun retrieveWaitQueue() {
        lateinit var userId: String
        repeat(2){
            userId = UUID.randomUUID().toString()
            val job = Job(event.eventId, userId, queueLimitTime = event.jobQueueLimitTime, jobQueueSize = event.jobQueueSize)
            waitQueueRedisRepository.register(job)
        }

        val accessToken = jwtUtils.create(event.eventId,userId ).accessToken

        val response = givenWithDocument.log().all()
            .filter(RetrieveWaitQueueDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .pathParam("eventId", event.eventId)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .get("/client/v1/events/{eventId}").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()

        assertThat(body.get<String>("status")).isEqualTo("WAIT")
        assertThat(body.get<Any>("expectedInfo.second")).isNotNull
        assertThat(body.get<Any>("expectedInfo.order")).isEqualTo(2)
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
            headerWithName(HttpHeaders.AUTHORIZATION).description("JWT token"),
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
            fieldWithPath("expectedInfo").type(JsonFieldType.OBJECT).description("대기 정보 객체").optional(),
            fieldWithPath("expectedInfo.second").type(JsonFieldType.NUMBER).description("대기 시간").optional(),
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
                .description("JWT"),
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
            fieldWithPath("expectedInfo").type(JsonFieldType.OBJECT).description("대기 정보 객체").optional(),
            fieldWithPath("expectedInfo.second").type(JsonFieldType.NUMBER).description("대기 시간").optional(),
            fieldWithPath("expectedInfo.order").type(JsonFieldType.NUMBER).description("대기 순번").optional(),
        )
    }
}
