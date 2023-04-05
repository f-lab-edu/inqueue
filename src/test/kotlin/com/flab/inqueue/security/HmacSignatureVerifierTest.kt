package com.flab.inqueue.security

import com.flab.inqueue.domain.customer.utils.CustomerAccountFactory
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class HmacSignatureVerifierTest {

    private val hmacSignatureVerifier: HmacSignatureVerifier = HmacSignatureVerifier()

    private val clientSecret = CustomerAccountFactory.generateClientSecret()

    @Test
    @DisplayName("전달받은 hmacSignature 검증")
    fun verifySignature() {
        val payload = "testMessage"

        val verifiedResult = hmacSignatureVerifier.verify(
            signature = getHmacSignatureFromClient(payload),
            clientSecret = clientSecret,
            payload = payload
        )

        Assertions.assertThat(verifiedResult).isTrue
    }

    private fun getHmacSignatureFromClient(payLoad: String): String {
        val sha256HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(clientSecret.toByteArray(), "HmacSHA256")
        sha256HMAC.init(secretKey)
        return Base64.encodeBase64String(sha256HMAC.doFinal(payLoad.toByteArray()))
    }
}