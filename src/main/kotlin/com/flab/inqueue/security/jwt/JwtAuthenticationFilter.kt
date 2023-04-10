package com.flab.inqueue.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager
) : OncePerRequestFilter() {

    companion object {
        private const val JWT_TOKEN_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwtToken = resolveJwtToken(request)

        if (jwtToken == null) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val jwtAuthenticationToken = JwtAuthenticationToken(null, jwtToken)
            val result = authenticationManager.authenticate(jwtAuthenticationToken)
            SecurityContextHolder.getContext().authentication = result
        } catch (e: AuthenticationException) {
            SecurityContextHolder.clearContext()
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveJwtToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!header.isNullOrEmpty() && header.startsWith(JWT_TOKEN_PREFIX)) {
            return header.substring(7)
        }

        return null
    }
}