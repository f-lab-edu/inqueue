package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.security.common.CommonPrincipal
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class HmacAuthenticationToken(
    val clientId: String? = null,
    val signature: String? = null,
    val payload: String? = null,
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
            clientId: String,
            signature: String,
            payload: String
        ): HmacAuthenticationToken {
            return HmacAuthenticationToken(
                clientId = clientId,
                signature = signature,
                payload = payload
            )
        }

        @JvmStatic
        fun authenticated(
            principal: CommonPrincipal,
            authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
        ): HmacAuthenticationToken {
            return HmacAuthenticationToken(
                clientId = principal.clientId,
                authenticated = true,
                principal = principal,
                authorities = authorities
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