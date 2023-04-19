package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.domain.member.utils.MemberKeyFactory
import com.flab.inqueue.security.hmacsinature.utils.HmacSignatureVerifier
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class HmacSignatureVerifierTest {

    private val hmacSignatureVerifier: HmacSignatureVerifier = HmacSignatureVerifier()

    private val memberKeyFactory: MemberKeyFactory = MemberKeyFactory(SecureRandom(), 32, 64)

    private val clientSecret1 = memberKeyFactory.generateClientSecret()

    private val clientSecret2 = memberKeyFactory.generateClientSecret()
    
    @Test
    @DisplayName("전달받은 hmacSignature 검증 성공")
    fun verify_signature_success() {
        val payload = "testMessage"

        val verifiedResult = hmacSignatureVerifier.verify(
            signature = getHmacSignatureFromClient(payload, clientSecret1),
            clientSecret = clientSecret1,
            payload = payload
        )

        Assertions.assertThat(verifiedResult).isTrue
    }

    @Test
    @DisplayName("clientSecret이 다른 경우, 전달받은 hmacSignature 검증 실패")
    fun verify_signature_fail() {
        val payload = "testMessage"

        val verifiedResult = hmacSignatureVerifier.verify(
            signature = getHmacSignatureFromClient(payload, clientSecret2),
            clientSecret = clientSecret1,
            payload = payload
        )

        Assertions.assertThat(verifiedResult).isFalse
    }


    private fun getHmacSignatureFromClient(payLoad: String, clientSecret: String): String {
        val sha256HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(clientSecret.toByteArray(), "HmacSHA256")
        sha256HMAC.init(secretKey)
        return Base64.encodeBase64String(sha256HMAC.doFinal(payLoad.toByteArray()))
    }
}