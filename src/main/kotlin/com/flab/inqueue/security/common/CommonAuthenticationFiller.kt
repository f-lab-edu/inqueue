package com.flab.inqueue.security.common

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter

abstract class CommonAuthenticationFiller(
    protected val authenticationManager: AuthenticationManager,
    vararg requestMatchers: RequestMatcher
) : OncePerRequestFilter() {

    private val orRequestMatcher: OrRequestMatcher = OrRequestMatcher(*requestMatchers)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            if (!isRequestMatched(request)) {
                filterChain.doFilter(request, response)
                return
            }

            val result = attemptAuthentication(request, response)
            SecurityContextHolder.getContext().authentication = result
        } catch (e: AuthenticationException) {
            SecurityContextHolder.clearContext()
        }
        filterChain.doFilter(request, response)
    }

    private fun isRequestMatched(request: HttpServletRequest): Boolean {
        return orRequestMatcher.matches(request)
    }

    abstract fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication

}