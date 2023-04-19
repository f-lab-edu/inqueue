package com.flab.inqueue.security.jwt.utils

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secretKey: String,
    val expirationMills: Long
)