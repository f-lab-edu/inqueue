package com.flab.inqueue.api

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.dto.*
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.*
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserTest : AcceptanceTest(){

    @Test
    @DisplayName("토큰 발급 api")
    fun tokenApiSpec() {
        val authRequest = AuthRequest("testEvent1")

        given().log().all()
            .header("Authorization", "ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post("v1/auth/token").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .body("accessToken", notNullValue())
    }

    @Test
    @DisplayName("사용자 대기열 진입 api")
    fun enterWaitQueue() {
        val eventId = "testEvent1"

        given().log().all()
            .header("Authorization", "AccessToken")
            .header("clientId", "String")
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post("v1/events/${eventId}/enter").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("status", notNullValue())
            .body("expectedInfo.time", notNullValue())
            .body("expectedInfo.order", notNullValue())
    }

    @Test
    @DisplayName("사용자 대기열 조회 api")
    fun retrieveWaitQueue() {
        val eventId = "testEvent1"

        given().log().all()
            .header("Authorization", "AccessToken")
            .header("clientId", "String")
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .get("v1/events/${eventId}").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("status", notNullValue())
            .body("expectedInfo.time", notNullValue())
            .body("expectedInfo.order", notNullValue())
    }
}