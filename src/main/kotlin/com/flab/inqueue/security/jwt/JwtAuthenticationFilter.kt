package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.common.CommonAuthenticationFiller
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.web.util.matcher.RequestMatcher

class JwtAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    vararg requestMatcher: RequestMatcher,
) : CommonAuthenticationFiller(authenticationManager, *requestMatcher) {

    companion object {
        private const val JWT_TOKEN_PREFIX = "Bearer "
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val jwtToken = resolveJwtToken(request) ?: throw BadCredentialsException("Invalid JWT authentication")

        val jwtAuthenticationToken = JwtAuthenticationToken.unauthenticated(jwtToken)
        return authenticationManager.authenticate(jwtAuthenticationToken)
    }

    private fun resolveJwtToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!header.isNullOrEmpty() && header.startsWith(JWT_TOKEN_PREFIX)) {
            return header.substring(7)
        }

        return null
    }
}