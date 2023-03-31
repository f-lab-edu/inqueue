package com.flab.inqueue.api

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.REST_DOCS_DOCUMENT_IDENTIFIER
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation
import org.springframework.restdocs.restassured.RestDocumentationFilter
import org.springframework.restdocs.snippet.Snippet

class QueueTest : AcceptanceTest() {

    @Test
    @DisplayName("사용자 작업열 검증 기능 api")
    fun validateJobQueue() {
        val eventId = "testEvent1"
        val userId = "testUser1"

        given.log().all()
            .filter(ValidateJobQueueDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "X-Client-Id:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .pathParam("eventId", eventId)
            .pathParam("userId", userId).
        `when`()
            .post("v1/events/{eventId}/job-queue-check/{userId}").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
    }

    @Test
    @DisplayName("사용자 작업열 종료 api")
    fun closeJopQueue() {
        val eventId = "testEvent1"
        val userId = "testUser1"

        given.log().all()
            .filter(CloseJopQueueDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "X-Client-Id:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .pathParam("eventId", eventId)
            .pathParam("userId", userId).
        `when`()
            .post("v1/events/{eventId}/job-queue-finish/{userId}").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
    }
}

object ValidateJobQueueDocument {
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