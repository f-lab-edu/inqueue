package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.jwt.utils.JwtUtils
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
        val verifyResponse = jwtUtils.verify(jwtAuthentication.jwtToken!!)

        if (!verifyResponse.isValid) {
            throw BadCredentialsException(
                "Invalid jwtToken - accessToken: ${authentication.jwtToken}",
                verifyResponse.throwable
            )
        }

        // TODO: 고객 검증 구현 - RDB 에 고객이 있는지 확인
        // TODO: 대기열 유저 검증 구현 - Redis UserList 에서 해당 유저가 있는지 확인

        return JwtAuthenticationToken.authenticatedToken(verifyResponse.userId)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}