package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.common.CommonAuthentication
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken(
    val clientId: String? = null,
    val userId: String? = null,
    val jwtToken: String? = null,
    isAuthenticated: Boolean = false,
    authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : CommonAuthentication(userId, userId, isAuthenticated, authorities) {

    companion object {
        @JvmStatic
        fun unauthenticatedToken(
            jwtToken: String
        ): JwtAuthenticationToken {
            return JwtAuthenticationToken(jwtToken = jwtToken)
        }

        @JvmStatic
        fun authenticatedToken(
            clientId: String,
            userId: String,
            authorities: MutableCollection<out GrantedAuthority>
        ): JwtAuthenticationToken {
            return JwtAuthenticationToken(
                clientId = clientId,
                userId = userId,
                isAuthenticated = true,
                authorities = authorities
            )
        }
    }
}