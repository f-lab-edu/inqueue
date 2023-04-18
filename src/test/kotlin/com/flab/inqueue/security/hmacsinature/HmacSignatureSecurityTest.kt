package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.domain.customer.entity.Customer
import com.flab.inqueue.domain.customer.repository.CustomerRepository
import com.flab.inqueue.domain.customer.utils.CustomerAccountFactory
import com.flab.inqueue.domain.dto.AuthRequest
import com.flab.inqueue.security.common.Role
import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
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
    lateinit var encryptionUtil: EncryptionUtil

    @Autowired
    lateinit var customerAccountFactory: CustomerAccountFactory

    lateinit var testClientIdWithUser: String

    lateinit var testClientSecretWithUser: String

    lateinit var testUser: Customer

    lateinit var hmacSignaturePayloadWithUser: String

    lateinit var testClientIdWithAdmin: String

    lateinit var testClientSecretWithAdmin: String

    lateinit var testAdmin: Customer

    lateinit var hmacSignaturePayloadWithAdmin: String

    companion object {
        const val HMAC_SECURITY_TEST_URI = "/server/hmac-security-test"
        const val HMAC_SECURITY_TEST_WITH_ADMIN_USER_URI = "/server/hmac-security-test-with-admin-role"
    }

    @BeforeEach
    @Transactional
    fun setUp(@LocalServerPort port: Int) {
        // ROLE_USER
        hmacSignaturePayloadWithUser = "http://localhost:${port}" + HMAC_SECURITY_TEST_URI
        testClientIdWithUser = customerAccountFactory.generateClientId()
        testClientSecretWithUser = customerAccountFactory.generateClientSecret()
        testUser = Customer("USER", testClientIdWithUser, testClientSecretWithUser)
        testUser.encryptClientSecret(encryptionUtil)
        customerRepository.save(testUser)

        // ROLE_ADMIN
        hmacSignaturePayloadWithAdmin = "http://localhost:${port}" + HMAC_SECURITY_TEST_WITH_ADMIN_USER_URI
        testClientIdWithAdmin = customerAccountFactory.generateClientId()
        testClientSecretWithAdmin = customerAccountFactory.generateClientSecret()
        testAdmin = Customer("ADMIN", testClientIdWithAdmin, testClientSecretWithAdmin, listOf(Role.USER, Role.ADMIN))
        testAdmin.encryptClientSecret(encryptionUtil)
        customerRepository.save(testAdmin)
    }

    @Test
    @DisplayName("Hmac Authentication 성공")
    fun hmac_authentication_success() {
        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(
                    testClientIdWithUser,
                    createHmacSignature(hmacSignaturePayloadWithUser, testClientSecretWithUser)
                )
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .get(HMAC_SECURITY_TEST_URI).
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("authenticated", Matchers.equalTo(true))
            .body("clientId", Matchers.equalTo(testClientIdWithUser))
            .body("signature", Matchers.nullValue())
            .body("payload", Matchers.nullValue())
            .body("credentials", Matchers.nullValue())
            .body("details", Matchers.nullValue())
            .body("principal.clientId", Matchers.equalTo(testClientIdWithUser))
            .body("principal.userId", Matchers.nullValue())
            .body("principal.roles", Matchers.hasItem("USER"))
            .body("authorities.authority", Matchers.hasItem("ROLE_USER"))
    }

    @Test
    @DisplayName("clientSecret이 다른 경우, Hmac Authentication 실패")
    fun hmac_authentication_fail1() {
        val eventId = "estEventId"
        val userId = "testUserId"

        val anotherClientSecret = customerAccountFactory.generateClientSecret()
        val authRequest = AuthRequest(eventId, userId)

        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(
                    testClientIdWithUser,
                    createHmacSignature(hmacSignaturePayloadWithUser, anotherClientSecret)
                )
            )
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post(HMAC_SECURITY_TEST_URI).
        then().log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .assertThat()
            .body("error", Matchers.equalTo("Unauthorized"))
            .body("timestamp", Matchers.notNullValue())
            .body("status", Matchers.notNullValue())
            .body("path", Matchers.notNullValue())
    }

    @Test
    @DisplayName("clientId를 찾을 수 없는 경우, Hmac Authentication 실패")
    fun hmac_authentication_fail2() {
        val eventId = "estEventId"
        val userId = "testUserId"

        val anotherClientId = customerAccountFactory.generateClientId()
        val authRequest = AuthRequest(eventId, userId)

        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(
                    anotherClientId,
                    createHmacSignature(hmacSignaturePayloadWithUser, testClientSecretWithUser)
                )
            )
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post(HMAC_SECURITY_TEST_URI).
        then().log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .assertThat()
            .body("error", Matchers.equalTo("Unauthorized"))
            .body("timestamp", Matchers.notNullValue())
            .body("status", Matchers.notNullValue())
            .body("path", Matchers.notNullValue())
    }

    @Test
    @DisplayName("AUTHORIZATION 헤더가 없는 경우, Hmac Authentication 실패")
    fun hmac_authentication_fail3() {
        val eventId = "estEventId"
        val userId = "testUserId"

        val authRequest = AuthRequest(eventId, userId)

        given.given().log().all()
            .body(authRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .post(HMAC_SECURITY_TEST_URI).
        then().log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .assertThat()
            .body("error", Matchers.equalTo("Unauthorized"))
            .body("timestamp", Matchers.notNullValue())
            .body("status", Matchers.notNullValue())
            .body("path", Matchers.notNullValue())
    }

    @Test
    @DisplayName("ADMIN권한을 요구하는 API에 ADMIN이 인증 요청했을때, Hmac Authorization 성공")
    fun hmac_authentication_with_admin_role_success() {
        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(
                    testClientIdWithAdmin,
                    createHmacSignature(hmacSignaturePayloadWithAdmin, testClientSecretWithAdmin)
                )
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .get(HMAC_SECURITY_TEST_WITH_ADMIN_USER_URI).
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("authenticated", Matchers.equalTo(true))
            .body("clientId", Matchers.equalTo(testClientIdWithAdmin))
            .body("signature", Matchers.nullValue())
            .body("payload", Matchers.nullValue())
            .body("credentials", Matchers.nullValue())
            .body("details", Matchers.nullValue())
            .body("principal.clientId", Matchers.equalTo(testClientIdWithAdmin))
            .body("principal.userId", Matchers.nullValue())
            .body("principal.roles", Matchers.hasItems("USER", "ADMIN"))
            .body("authorities.authority", Matchers.hasItems("ROLE_USER", "ROLE_ADMIN"))
    }

    @Test
    @DisplayName("ADMIN권한을 요구하는 API에 USER가 인증 요청했을때, Hmac Authorization 실패")
    fun hmac_authentication_with_admin_role_fail() {
        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(
                    testClientIdWithUser,
                    createHmacSignature(hmacSignaturePayloadWithAdmin, testClientSecretWithUser)
                )
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .get(HMAC_SECURITY_TEST_WITH_ADMIN_USER_URI).
        then().log().all()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .assertThat()
            .body("error", Matchers.equalTo("Forbidden"))
            .body("timestamp", Matchers.notNullValue())
            .body("status", Matchers.notNullValue())
            .body("path", Matchers.notNullValue())
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