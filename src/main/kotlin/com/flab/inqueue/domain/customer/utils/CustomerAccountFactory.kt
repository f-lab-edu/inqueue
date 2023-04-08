package com.flab.inqueue.domain.customer.utils

import java.security.SecureRandom
import java.util.*

class CustomerAccountFactory(
    private val secureRandom: SecureRandom,
    private val clientIdLength: Int,
    private val clientSecretLength: Int
) {
    fun generateClientId(): String {
        val bytes = ByteArray(clientIdLength)
        secureRandom.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun generateClientSecret(): String {
        val bytes = ByteArray(clientSecretLength)
        secureRandom.nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }
}
