package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.security.common.CommonAuthentication
import org.springframework.security.core.GrantedAuthority

class HmacAuthenticationToken(
    val clientId: String? = null,
    val signature: String? = null,
    val payload: String? = null,
    authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : CommonAuthentication(clientId, clientId, false, authorities) {

    companion object {
        @JvmStatic
        fun authenticatedToken(
            clientId: String,
            authorities: MutableCollection<out GrantedAuthority>
        ): HmacAuthenticationToken {
            return HmacAuthenticationToken(
                clientId = clientId,
                authorities = authorities
            ).apply { isAuthenticated = true }
        }
    }
}