package com.flab.inqueue.security.jwt.utils

import java.time.LocalDateTime

data class JwtToken(
    val accessToken: String,
    val expiration: LocalDateTime
)