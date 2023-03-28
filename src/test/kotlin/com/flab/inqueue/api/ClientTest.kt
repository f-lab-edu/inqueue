package com.flab.inqueue.api

import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.*
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest
class ClientTest {


    @Test
    @DisplayName("행사 도메인 CRUD")
    fun createEvent() {
        val body = hashMapOf<String, String>()
        body["name"] = "name"
        body["description"] = "description"
        body["place"] = "place"
        body["time"] = "time"
        body["startTime"] = "startTime"
        body["endTime"] = "endTime"
        body["personnel"] = "personnel"
        body["waitQueueStartTime"] = "waitQueueStartTime"
        body["waitQueueEndTime"] = "waitQueueEndTime"
        body["type"] = "type"
        body["jobQueueSize"] = "jobQueueSize"
        body["jobQueueLimitTime"] = "jobQueueLimitTime"
        body["redirectUrl"] = "redirectUrl"

        val response = given().log().all()
            .header("Authorization","ClientId:(StringToSign를 ClientSecret으로 Hmac 암호화)")
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .`when`().post("v1/event")
            .then().log().all()
            .assertThat()
            .body("eventId", equalTo("UUID"))
    }
}