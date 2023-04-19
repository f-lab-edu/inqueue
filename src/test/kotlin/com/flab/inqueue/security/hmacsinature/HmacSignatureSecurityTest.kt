package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.AcceptanceTest
import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.repository.MemberRepository
import com.flab.inqueue.domain.member.utils.memberkeygenrator.MemberKeyGenerator
import com.flab.inqueue.domain.dto.AuthRequest
import com.flab.inqueue.domain.member.entity.MemberKey
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
    lateinit var memberRepository: MemberRepository
    @Autowired
    lateinit var encryptionUtil: EncryptionUtil
    @Autowired
    lateinit var memberKeyGenerator: MemberKeyGenerator

    // ROLE_USER
    lateinit var testUser: Member
    lateinit var notEncryptedUserMemberKey: MemberKey
    lateinit var hmacSignaturePayloadWithUser: String

    // ROLE_ADMIN
    lateinit var testAdmin: Member
    lateinit var notEncryptedAdminMemberKey: MemberKey
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
        notEncryptedUserMemberKey = memberKeyGenerator.generate()
        testUser = Member(
            "USER",
            MemberKey(notEncryptedUserMemberKey.clientId, notEncryptedUserMemberKey.clientSecret)
        )
        testUser.encryptMemberKey(encryptionUtil)
        memberRepository.save(testUser)

        // ROLE_ADMIN
        hmacSignaturePayloadWithAdmin = "http://localhost:${port}" + HMAC_SECURITY_TEST_WITH_ADMIN_USER_URI
        notEncryptedAdminMemberKey = memberKeyGenerator.generate()
        testAdmin = Member(
            "ADMIN",
            MemberKey(notEncryptedAdminMemberKey.clientId, notEncryptedAdminMemberKey.clientSecret),
            listOf(Role.USER, Role.ADMIN)
        )
        testAdmin.encryptMemberKey(encryptionUtil)
        memberRepository.save(testAdmin)
    }

    @Test
    @DisplayName("Hmac Authentication 성공")
    fun hmac_authentication_success() {
        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(
                    notEncryptedUserMemberKey.clientId,
                    createHmacSignature(hmacSignaturePayloadWithUser, notEncryptedUserMemberKey.clientSecret)
                )
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .get(HMAC_SECURITY_TEST_URI).
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("authenticated", Matchers.equalTo(true))
            .body("clientId", Matchers.equalTo(notEncryptedUserMemberKey.clientId))
            .body("signature", Matchers.nullValue())
            .body("payload", Matchers.nullValue())
            .body("credentials", Matchers.nullValue())
            .body("details", Matchers.nullValue())
            .body("principal.clientId", Matchers.equalTo(notEncryptedUserMemberKey.clientId))
            .body("principal.userId", Matchers.nullValue())
            .body("principal.roles", Matchers.hasItem("USER"))
            .body("authorities.authority", Matchers.hasItem("ROLE_USER"))
    }

    @Test
    @DisplayName("clientSecret이 다른 경우, Hmac Authentication 실패")
    fun hmac_authentication_fail1() {
        val eventId = "estEventId"
        val userId = "testUserId"

        val anotherMemberKey = memberKeyGenerator.generate()
        val authRequest = AuthRequest(eventId, userId)

        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(
                    notEncryptedUserMemberKey.clientId,
                    createHmacSignature(hmacSignaturePayloadWithUser, anotherMemberKey.clientSecret)
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

        val anotherMemberKey = memberKeyGenerator.generate()
        val authRequest = AuthRequest(eventId, userId)

        given.log().all()
            .header(
                HttpHeaders.AUTHORIZATION,
                createAuthorization(
                    anotherMemberKey.clientId,
                    createHmacSignature(hmacSignaturePayloadWithUser, anotherMemberKey.clientSecret)
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
                    notEncryptedAdminMemberKey.clientId,
                    createHmacSignature(hmacSignaturePayloadWithAdmin, notEncryptedAdminMemberKey.clientSecret)
                )
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE).
        `when`()
            .get(HMAC_SECURITY_TEST_WITH_ADMIN_USER_URI).
        then().log().all()
            .statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("authenticated", Matchers.equalTo(true))
            .body("clientId", Matchers.equalTo(notEncryptedAdminMemberKey.clientId))
            .body("signature", Matchers.nullValue())
            .body("payload", Matchers.nullValue())
            .body("credentials", Matchers.nullValue())
            .body("details", Matchers.nullValue())
            .body("principal.clientId", Matchers.equalTo(notEncryptedAdminMemberKey.clientId))
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
                    notEncryptedUserMemberKey.clientId,
                    createHmacSignature(hmacSignaturePayloadWithAdmin, notEncryptedUserMemberKey.clientSecret)
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