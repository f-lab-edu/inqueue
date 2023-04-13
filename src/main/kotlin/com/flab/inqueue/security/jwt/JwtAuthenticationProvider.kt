package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.jwt.utils.JwtUtils
import com.flab.inqueue.security.jwt.utils.JwtVerificationResponse
import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

@Component
class JwtAuthenticationProvider(
    private val jwtUtils: JwtUtils,
) : AuthenticationProvider {

    companion object {
        private const val DEFAULT_USER_ROLE = "ROLE_USER"
    }

    override fun authenticate(authentication: Authentication?): Authentication {
        val jwtAuthentication = authentication as JwtAuthenticationToken

        val verifyResponse: JwtVerificationResponse
        try {
            verifyResponse = jwtUtils.verify(jwtAuthentication.jwtToken!!)
        } catch (e: JwtException) {
            throw BadCredentialsException("Invalid jwtToken - accessToken: ${authentication.jwtToken}", e)
        }

        // TODO: 대기열 유저 검증 구현 - Redis UserList 에서 해당 유저가 있는지 확인

        return JwtAuthenticationToken.authenticatedToken(
            clientId = verifyResponse.clientId,
            userId = verifyResponse.userId,
            authorities = mutableListOf(SimpleGrantedAuthority(DEFAULT_USER_ROLE))
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}