package com.flab.inqueue.api

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.dto.EventRequest
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.*
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class ClientTest : AcceptanceTest() {

    @Test
    fun test(){
    }

    @Test
    @DisplayName("행사 도메인 CRUD")
    fun createEvent() {
        val eventRequest = EventRequest(
            "name",
            "description",
            "place",
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

        given().log().all()
            .header("Authorization", "ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(eventRequest).
        `when`()
            .post("v1/events").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("eventId", notNullValue())
    }
}