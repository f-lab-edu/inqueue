package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.security.common.CommonAuthentication
import com.flab.inqueue.security.common.CommonPrincipal
import com.flab.inqueue.security.common.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class HmacAuthenticationToken(
    val clientId: String? = null,
    val signature: String? = null,
    val payload: String? = null,
    principal: CommonPrincipal? = null,
    isAuthenticated: Boolean = false,
    authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : CommonAuthentication(clientId, principal, isAuthenticated, authorities) {

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
            clientId: String,
            roles: List<Role>
        ): HmacAuthenticationToken {
            val principal = CommonPrincipal(clientId = clientId, roles = roles)
            return HmacAuthenticationToken(
                clientId = clientId,
                authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }.toMutableList(),
                isAuthenticated = true,
                principal = principal
            )
        }
    }
}