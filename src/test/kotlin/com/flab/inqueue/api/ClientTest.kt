package com.flab.inqueue.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.flab.inqueue.dto.EventRequest
import com.flab.inqueue.dto.EventResponse
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.time.LocalDateTime

@SpringBootTest
class ClientTest {


    @Test
    @DisplayName("행사 도메인 CRUD")
    fun createEvent() {
        val eventRequest = EventRequest(
            "name",
            "description",
            "place",
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            10L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            "type",
            10L,
            10L,
            "redirectUrl"
        )

        val response =
            given().log().all()
                .header("Authorization", "ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(eventRequest)
            .`when`()
                .post("v1/event")
            .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()

        val eventReponse = ObjectMapper().readValue(response.body().asString(), EventResponse::class.java)

        assertThat(eventReponse.eventId).isNotNull
    }
}