package com.flab.inqueue.domain.auth.dto

import com.flab.inqueue.security.jwt.utils.JwtToken


data class TokenResponse(
    val userId: String,
    val token: JwtToken
)

