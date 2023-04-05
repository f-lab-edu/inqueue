package com.flab.inqueue.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter


class HmacSignatureFilter(
    private val authenticationManager: AuthenticationManager
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authorization.isNullOrEmpty() && !authorization.contains(":")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val (clientId, signature) = authorization.split(":")
            val hmacAuthenticationToken = HmacAuthenticationToken(clientId, signature, request.requestURL.toString())
            val result = authenticationManager.authenticate(hmacAuthenticationToken)
            SecurityContextHolder.getContext().authentication = result
        } catch (e: AuthenticationException) {
            SecurityContextHolder.clearContext()
        }
        filterChain.doFilter(request, response)
    }
}