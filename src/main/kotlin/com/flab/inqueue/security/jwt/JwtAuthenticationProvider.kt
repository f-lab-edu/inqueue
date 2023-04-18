package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.common.CommonPrincipal
import com.flab.inqueue.security.common.Role
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

    override fun authenticate(authentication: Authentication?): Authentication {
        val jwtAuthentication = authentication as JwtAuthenticationToken

        val verifyResponse: JwtVerificationResponse
        try {
            verifyResponse = jwtUtils.verify(jwtAuthentication.jwtToken!!)
        } catch (e: JwtException) {
            throw BadCredentialsException("Invalid jwtToken - accessToken: ${authentication.jwtToken}", e)
        }

        val principal = CommonPrincipal(
            clientId = verifyResponse.clientId,
            userId = verifyResponse.userId,
            roles = listOf(Role.USER)
        )

        return JwtAuthenticationToken.authenticated(
            principal = principal,
            authorities = listOf(Role.USER).map { SimpleGrantedAuthority("ROLE_$it") }.toMutableList()
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}