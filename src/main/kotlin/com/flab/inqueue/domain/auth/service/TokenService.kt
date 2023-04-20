package com.flab.inqueue.domain.auth.service

import com.flab.inqueue.domain.auth.dto.TokenRequest
import com.flab.inqueue.security.jwt.utils.JwtToken
import com.flab.inqueue.security.jwt.utils.JwtUtils
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val jwtUtils: JwtUtils
) {
    fun generateToken(tokenRequest: TokenRequest): JwtToken {
        return jwtUtils.create(tokenRequest.clientId!!, tokenRequest.userId)
    }
}