package com.flab.inqueue.security.jwt.utils

data class JwtVerificationResponse(
    val clientId: String? = null,
    val userId: String? = null,
    val isValid: Boolean,
    val throwable: Throwable? = null
)