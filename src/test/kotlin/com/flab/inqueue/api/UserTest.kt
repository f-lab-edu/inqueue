package com.flab.inqueue.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.flab.inqueue.dto.*
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.*
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest
class UserTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("토큰 발급 api")
    fun tokenApiSpec() {
        val authRequest = AuthRequest("testEvent1")

        val response = given().log().all()
            .header("Authorization","ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .`when`()
            .post("v1/auth/token")
        .then().log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()

        val authResponse = objectMapper.readValue(response.body().asString(), AuthResponse::class.java)

        assertThat(authResponse.accessToken).isEqualTo("JWT TOKEN")
    }
    @Test
    @DisplayName("사용자 대기열 진입 api")
    fun enterWaitQueue() {
        var eventId = "testEvent1"

        val response = given().log().all()
                .header("Authorization","AccessToken")
                .header("clientId","String")
                .pathParam("id",eventId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .`when`()
                .post("v1/events/${eventId}/enter")
            .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()

        val eventReponse = objectMapper.readValue(response.body().asString(), QueueResponse::class.java)

        assertThat(eventReponse.status).isNotNull
        assertThat(eventReponse.expectedInfo.time).isNotNull
        assertThat(eventReponse.expectedInfo.order).isNotNull
    }

    @Test
    @DisplayName("사용자 대기열 조회 api")
    fun retriveveWaitQueue() {

        var eventId = "testEvent1"

        val response = given().log().all()
                .header("Authorization", "AccessToken")
                .header("clientId", "String")
                .pathParam("id", eventId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .`when`()
                .get("v1/events/${eventId}")
            .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()

        val eventReponse = objectMapper.readValue(response.body().asString(), QueueResponse::class.java)

        assertThat(eventReponse.status).isNotNull
        assertThat(eventReponse.expectedInfo.time).isNotNull
        assertThat(eventReponse.expectedInfo.order).isNotNull
    }
}