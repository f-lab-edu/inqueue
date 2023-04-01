package com.flab.inqueue.api

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.REST_DOCS_DOCUMENT_IDENTIFIER
import com.flab.inqueue.domain.event.dto.EventRequest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document
import org.springframework.restdocs.restassured.RestDocumentationFilter
import org.springframework.restdocs.snippet.Snippet
import java.time.LocalDateTime

class ClientTest : AcceptanceTest() {

    @Test
    @DisplayName("행사 도메인 CRUD")
    fun createEvent() {
        val eventRequest = EventRequest(
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            1L,
            null,
            null
        )

        val response = given.log().all()
            .filter(CreateEventDocument.FILTER)
            .header(HttpHeaders.AUTHORIZATION, "X-Client-Id:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(eventRequest).
        `when`()
            .post("v1/events").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val body = response.body().jsonPath()

        assertThat(body.get<String>("eventId")).isEqualTo("eventId")
    }


}

object CreateEventDocument {
    val FILTER: RestDocumentationFilter = document(
        REST_DOCS_DOCUMENT_IDENTIFIER,
        headerFiledSnippet(),
        requestFieldsSnippet(),
        responseFieldsSnippet()
    )

    private fun headerFiledSnippet() : Snippet {
        return requestHeaders(
            headerWithName(HttpHeaders.AUTHORIZATION).description("X-Client-Id:(StringToSign를 ClientSecret으로 Hmac 암호화)"),
            headerWithName(HttpHeaders.CONTENT_TYPE).description("요청-Type")
        )
    }

    private fun requestFieldsSnippet(): Snippet {
        return requestFields(
            fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
            fieldWithPath("description").type(JsonFieldType.STRING).description("설명"),
            fieldWithPath("place").type(JsonFieldType.STRING).description("장소"),
            fieldWithPath("startTime").type(JsonFieldType.ARRAY).description("행사 시작 시간"),
            fieldWithPath("endTime").type(JsonFieldType.ARRAY).description("행사 마침 시간"),
            fieldWithPath("personnel").type(JsonFieldType.NUMBER).description("인원"),
            fieldWithPath("waitQueueStartTime").type(JsonFieldType.ARRAY).description("대기 큐 시작 시간"),
            fieldWithPath("waitQueueEndTime").type(JsonFieldType.ARRAY).description("대기 큐 마침 시간"),
            fieldWithPath("type").type(JsonFieldType.STRING).description("행사 종류"),
            fieldWithPath("jobQueueSize").type(JsonFieldType.NUMBER).description("작업 큐 크기"),
            fieldWithPath("jobQueueLimitTime").type(JsonFieldType.NUMBER).description("작업 큐 제한 시간"),
            fieldWithPath("redirectUrl").type(JsonFieldType.STRING).description("redirectUrl"),
        )
    }

    private fun responseFieldsSnippet(): Snippet {
        return responseFields(
            fieldWithPath("eventId")
                .type(JsonFieldType.STRING)
                .description("이벤트 식별자"),
        )
    }
}
