package com.flab.inqueue.domain.customer.utils

import java.security.SecureRandom
import java.util.*

object CustomerAccountFactory {

    private const val DEFAULT_CLIENT_ID_LENGTH = 32

    private const val DEFAULT_CLIENT_SECRET_LENGTH = 64

    fun generateClientId(): String {
        val bytes = ByteArray(DEFAULT_CLIENT_ID_LENGTH)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun generateClientSecret(): String {
        val bytes = ByteArray(DEFAULT_CLIENT_SECRET_LENGTH)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }
}
