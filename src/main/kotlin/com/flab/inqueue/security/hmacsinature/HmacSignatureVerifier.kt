package com.flab.inqueue.security.hmacsinature

import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class HmacSignatureVerifier {

    companion object {
        private const val SIGNATURE_ALGORITHM = "HmacSHA256"
    }

    fun verify(signature: String, clientSecret: String, payload: String): Boolean {
        val madeSignature = getHmacSignature(clientSecret, payload.toByteArray())
        return signature.trim() == madeSignature
    }

    private fun getHmacSignature(clientSecret: String, payload: ByteArray): String {
        val key = clientSecret.toByteArray()
        val secretKeySpec = SecretKeySpec(key, SIGNATURE_ALGORITHM)

        val sha256HMAC = Mac.getInstance(SIGNATURE_ALGORITHM)
        sha256HMAC.init(secretKeySpec)
        return Base64.getEncoder().encodeToString(sha256HMAC.doFinal(payload))
    }
}