package com.flab.inqueue.api

import com.flab.inqueue.AcceptanceTest
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class QueueTest : AcceptanceTest(){
    @Test
    @DisplayName("사용자 작업열 검증 기능 api")
    fun validateJobQueue() {

        val eventId = "testEvent1"
        val userId = "testUser1"

        given().log().all()
            .header("Authorization", "ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post("v1/event/${eventId}/job-queue-check/${userId}").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
    }

    @Test
    @DisplayName("사용자 작업열 종료 api")
    fun closeJopQueue() {
        val eventId = "testEvent1"
        val userId = "testUser1"

        given().log().all()
            .header("Authorization", "ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post("v1/event/${eventId}/job-queue-finish/${userId}").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
    }
}