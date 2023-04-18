package com.flab.inqueue.security.jwt

import com.flab.inqueue.security.common.CommonPrincipal
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken(
    val jwtToken: String? = null,
    val principal: CommonPrincipal? = null,
    authenticated: Boolean = false,
    authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : AbstractAuthenticationToken(authorities) {

    init {
        isAuthenticated = authenticated
    }

    companion object {
        @JvmStatic
        fun unauthenticated(
            jwtToken: String
        ): JwtAuthenticationToken {
            return JwtAuthenticationToken(jwtToken = jwtToken)
        }

        @JvmStatic
        fun authenticated(
            principal: CommonPrincipal?,
            authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
        ): JwtAuthenticationToken {
            return JwtAuthenticationToken(
                authenticated = true,
                authorities = authorities,
                principal = principal,
            )
        }
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return this.principal
    }
}