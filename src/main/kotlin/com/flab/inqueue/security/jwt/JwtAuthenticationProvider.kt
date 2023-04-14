package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.common.Role
import com.flab.inqueue.security.jwt.utils.JwtUtils
import com.flab.inqueue.security.jwt.utils.JwtVerificationResponse
import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(
    private val jwtUtils: JwtUtils,
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication {
        val jwtAuthentication = authentication as JwtAuthenticationToken

        val verifyResponse: JwtVerificationResponse
        try {
            verifyResponse = jwtUtils.verify(jwtAuthentication.jwtToken!!)
        } catch (e: JwtException) {
            throw BadCredentialsException("Invalid jwtToken - accessToken: ${authentication.jwtToken}", e)
        }

        // TODO: 대기열 유저 검증 구현 - Redis UserList 에서 해당 유저가 있는지 확인

        return JwtAuthenticationToken.authenticated(
            clientId = verifyResponse.clientId,
            userId = verifyResponse.userId,
            roles = listOf(Role.USER)
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}