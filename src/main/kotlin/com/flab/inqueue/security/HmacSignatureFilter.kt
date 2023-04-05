package com.flab.inqueue.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class HmacSignatureFilter(
    private val customerUserDetailsService: CustomerUserDetailsService,
    private val hmacSignatureVerifier: HmacSignatureVerifier

) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authorizationHeader.isNullOrEmpty() || !authorizationHeader.contains(":")) {
            filterChain.doFilter(request, response)
            return
        }

        val (clientId, signature) = authorizationHeader.split(":")
        val userDetails: UserDetails = customerUserDetailsService.loadUserByUsername(clientId)

        if (hmacSignatureVerifier.verify(signature, userDetails.password, request.requestURL.toString())) {
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}