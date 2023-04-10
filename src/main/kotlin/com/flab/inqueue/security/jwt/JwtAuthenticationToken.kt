package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.common.CustomAuthentication
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken(
    userId: String? = null,
    val jwtToken: String? = null,
    authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : CustomAuthentication(userId, userId, false, authorities) {

    companion object {
        @JvmStatic
        fun authenticatedToken(
            userId: String?
        ): JwtAuthenticationToken {
            return JwtAuthenticationToken(
                userId = userId
            ).apply { isAuthenticated = true }
        }
    }
}