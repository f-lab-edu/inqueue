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

        return JwtAuthenticationToken.authenticatedToken(verifyResponse.userId)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}