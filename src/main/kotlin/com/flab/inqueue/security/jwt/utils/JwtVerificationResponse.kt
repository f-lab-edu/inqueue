package com.flab.inqueue.security.jwt.utils

data class JwtVerificationResponse(
    val clientId: String,
    val userId: String,
)