package com.flab.inqueue.domain.auth.service

import com.flab.inqueue.domain.auth.dto.TokenRequest
import com.flab.inqueue.domain.auth.dto.TokenResponse
import com.flab.inqueue.security.jwt.utils.JwtUtils
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService(
    private val jwtUtils: JwtUtils
) {
    fun generateToken(tokenRequest: TokenRequest): TokenResponse {
        val userId = UUID.randomUUID().toString()
        val jwtToken = jwtUtils.create(tokenRequest.clientId, userId)
        return TokenResponse(userId, jwtToken)
    }
}