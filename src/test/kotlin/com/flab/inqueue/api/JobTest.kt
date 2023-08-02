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
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation
import org.springframework.restdocs.restassured.RestDocumentationFilter
import org.springframework.restdocs.snippet.Snippet
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

class JobTest : AcceptanceTest() {

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var encryptionUtil: EncryptionUtil

    @Autowired
    lateinit var memberKeyGenerator: MemberKeyGenerator

    @Autowired
    lateinit var jobRedisRepository: JobRedisRepository

    var port: Int = 0
    lateinit var member: Member
    lateinit var notEncryptedUserMemberKey: MemberKey
    lateinit var event: Event
    lateinit var job: Job
    val userId = UUID.randomUUID()!!.toString()

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
        job = Job(event.eventId, userId, JobStatus.ENTER, event.jobQueueLimitTime, event.jobQueueSize)
        jobRedisRepository.register(job)
    }

    @Test
    @DisplayName("사용자 작업열 검증 기능 api")
    fun validateJobQueue() {
        val hmacSignaturePayload =
            "http://localhost:${port}/server/v1/events/${event.eventId}/job-queue-check/${userId}"

        val response = givenWithDocument.log().all()
            .filter(ValidateJobQueueDocument.FILTER)
            .header(
                HttpHeaders.AUTHORIZATION,
                createHmacAuthorizationHeader(
                    member.key.clientId, notEncryptedUserMemberKey.clientSecret, hmacSignaturePayload
                )
            )
            .pathParam("eventId", event.eventId)
            .pathParam("userId", userId).
        `when`()
            .post("/server/v1/events/{eventId}/job-queue-check/{userId}").then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()

        assertThat(body.get<Boolean>("isVerified")).isTrue()
    }

    @Test
    @DisplayName("사용자 작업열 종료 api")
    fun closeJopQueue() {
        val hmacSignaturePayload =
            "http://localhost:${port}/server/v1/events/${event.eventId}/job-queue-finish/${userId}"

        givenWithDocument.log().all()
            .filter(CloseJopQueueDocument.FILTER)
            .header(
                HttpHeaders.AUTHORIZATION,
                createHmacAuthorizationHeader(
                    member.key.clientId, notEncryptedUserMemberKey.clientSecret, hmacSignaturePayload
                )
            )
            .pathParam("eventId", event.eventId)
            .pathParam("userId", userId).
        `when`()
            .post("/server/v1/events/{eventId}/job-queue-finish/{userId}").then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
    }
}

object ValidateJobQueueDocument {
    val FILTER: RestDocumentationFilter = RestAssuredRestDocumentation.document(
        REST_DOCS_DOCUMENT_IDENTIFIER,
        headerFiledSnippet(),
        pathParametersSnippet(),
        responseFieldsSnippet()
    )

    private fun headerFiledSnippet(): Snippet {
        return requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("X-Client-Id:(StringToSign를 ClientSecret으로 Hmac 암호화)"),
            headerWithName(HttpHeaders.CONTENT_TYPE).description("요청-Type")
        )
    }

    private fun pathParametersSnippet(): Snippet {
        return pathParameters(
            parameterWithName("eventId").description("이벤트 식별자"),
            parameterWithName("userId").description("유저 식별자"),
        )
    }

    private fun responseFieldsSnippet(): Snippet {
        return responseFields(fieldWithPath("isVerified").type(JsonFieldType.BOOLEAN).description("작업열 검증 결과"),
        )
    }
}

object CloseJopQueueDocument {
    val FILTER: RestDocumentationFilter = RestAssuredRestDocumentation.document(
        REST_DOCS_DOCUMENT_IDENTIFIER,
        headerFiledSnippet(),
        pathParametersSnippet()
    )

    private fun headerFiledSnippet(): Snippet {
        return requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("X-Client-Id:(StringToSign를 ClientSecret으로 Hmac 암호화)"),
            headerWithName(HttpHeaders.CONTENT_TYPE).description("요청-Type")
        )
    }

    private fun pathParametersSnippet(): Snippet {
        return pathParameters(
            parameterWithName("eventId").description("이벤트 식별자"),
            parameterWithName("userId").description("유저 식별자"),
        )
    }
}