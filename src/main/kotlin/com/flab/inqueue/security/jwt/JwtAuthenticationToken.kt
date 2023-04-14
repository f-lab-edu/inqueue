package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.common.CommonAuthentication
import com.flab.inqueue.security.common.CommonPrincipal
import com.flab.inqueue.security.common.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class JwtAuthenticationToken(
    val clientId: String? = null,
    val userId: String? = null,
    val jwtToken: String? = null,
    principal: CommonPrincipal? = null,
    isAuthenticated: Boolean = false,
    authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : CommonAuthentication(userId, principal, isAuthenticated, authorities) {

    companion object {
        @JvmStatic
        fun unauthenticated(
            jwtToken: String
        ): JwtAuthenticationToken {
            return JwtAuthenticationToken(jwtToken = jwtToken)
        }

        @JvmStatic
        fun authenticated(
            clientId: String,
            userId: String,
            roles: List<Role>,
        ): JwtAuthenticationToken {
            val principal = CommonPrincipal(clientId = clientId, userId = userId, roles = roles)
            return JwtAuthenticationToken(
                clientId = clientId,
                userId = userId,
                isAuthenticated = true,
                authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }.toMutableList(),
                principal = principal
            )
        }
    }
}