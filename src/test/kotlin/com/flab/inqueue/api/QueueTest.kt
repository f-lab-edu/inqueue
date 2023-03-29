package com.flab.inqueue.api

import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest
class QueueTest {
    @Test
    @DisplayName("사용자 작업열 검증 기능 api")
    fun validateJobQueue() {

        var eventId = "testEvent1"

        val response = given().log().all()
                .header("Authorization","ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
                .pathParam("id",eventId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .`when`()
                .post("v1/jobs/{eventId}/validate")
            .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
    }

    @Test
    @DisplayName("사용자 작업열 종료 api")
    fun closeJopQueue() {
        var eventId = "testEvent1"

        val response = given().log().all()
                .header("Authorization","ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
                .pathParam("id",eventId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .`when`()
                .post("v1/jobs/{eventId}/finish")
            .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
    }

}