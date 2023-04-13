package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.security.common.CommonAuthenticationFiller
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.web.util.matcher.RequestMatcher


class HmacSignatureAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    vararg requestMatcher: RequestMatcher,
) : CommonAuthenticationFiller(authenticationManager, *requestMatcher) {


    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authorization.isNullOrEmpty() || !authorization.contains(":")) {
            throw BadCredentialsException("Invalid Hmac authentication : $authorization")
        }

        val (clientId, signature) = authorization.split(":")
        val authentication = HmacAuthenticationToken.unauthenticatedToken(
                clientId = clientId,
                signature = signature,
                payload = request.requestURL.toString()
            )
        return authenticationManager.authenticate(authentication)
    }

}