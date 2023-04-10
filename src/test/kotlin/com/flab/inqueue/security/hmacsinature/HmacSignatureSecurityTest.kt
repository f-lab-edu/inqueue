package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.domain.customer.entity.Customer
import com.flab.inqueue.domain.customer.repository.CustomerRepository
import com.flab.inqueue.domain.customer.utils.CustomerAccountFactory
import com.flab.inqueue.domain.dto.AuthRequest
import com.flab.inqueue.security.hmacsinature.utils.SecretKeyCipher
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64
import jakarta.transaction.Transactional
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class HmacSignatureSecurityTest : AcceptanceTest() {

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var secretKeyCipher: SecretKeyCipher

    @Autowired
    lateinit var customerAccountFactory: CustomerAccountFactory

    lateinit var testClientId: String

    lateinit var testClientSecret: String

    lateinit var testUser: Customer

    lateinit var generateTokenURL: String


    @BeforeEach
    @Transactional
    fun setUp(@LocalServerPort port: Int) {
        generateTokenURL = "http://localhost:${port}/v1/auth/token"
        testClientId = customerAccountFactory.generateClientId()
        testClientSecret = customerAccountFactory.generateClientSecret()
        testUser = Customer.user("USER", testClientId, testClientSecret)
        testUser.encryptClientSecret(secretKeyCipher)
        customerRepository.save(testUser)
    }

    @Test
    @DisplayName("JWT 토큰 발급 성공")
    fun generate_token_success() {
        val eventId = "estEventId"
        val userId = "testUserId"

        val authRequest = AuthRequest(eventId, userId)

        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(testClientId, createHmacSignature(generateTokenURL, testClientSecret))
            )
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post("/v1/auth/token").
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("accessToken", Matchers.notNullValue())
    }


    @Test
    @DisplayName("clientSecret이 다른 경우, JWT 토큰 발급 실패")
    fun generate_token_fail() {
        val eventId = "estEventId"
        val userId = "testUserId"

        val anotherClientSecret = customerAccountFactory.generateClientSecret()
        val authRequest = AuthRequest(eventId, userId)

        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(testClientId, createHmacSignature(generateTokenURL, anotherClientSecret))
            )
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post("/v1/auth/token").
        then().log().all()
            .statusCode(HttpStatus.FORBIDDEN.value())
    }


    @Test
    @DisplayName("clientId를 찾을 수 없는 경우, JWT 토큰 발급 실패")
    fun generate_token_fail2() {
        val eventId = "estEventId"
        val userId = "testUserId"

        val anotherClientId = customerAccountFactory.generateClientId()
        val authRequest = AuthRequest(eventId, userId)

        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(anotherClientId, createHmacSignature(generateTokenURL, testClientSecret))
            )
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post("/v1/auth/token").
        then().log().all()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .assertThat()
            .body("error", Matchers.equalTo("Forbidden"))
    }


    @Test
    @DisplayName("AUTHORIZATION 헤더가 없는 경우, JWT 토큰 발급 실패")
    fun generate_token_fail3() {
        val eventId = "estEventId"
        val userId = "testUserId"

        val authRequest = AuthRequest(eventId, userId)

        given.given().log().all()
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).`when`()
            .post("/v1/auth/token").then().log().all()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .assertThat()
            .body("error", Matchers.equalTo("Forbidden"))
    }

    private fun createHmacSignature(payLoad: String, clientSecret: String): String {
        val sha256HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(clientSecret.toByteArray(), "HmacSHA256")
        sha256HMAC.init(secretKey)
        return Base64.encodeBase64String(sha256HMAC.doFinal(payLoad.toByteArray()))
    }

    private fun createAuthorization(clientId: String, hmacSignature: String): String {
        return "$clientId:$hmacSignature"
    }
}