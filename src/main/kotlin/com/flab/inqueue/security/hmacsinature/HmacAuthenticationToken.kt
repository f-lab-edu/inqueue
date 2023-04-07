package com.flab.inqueue.security.hmacsinature

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class HmacAuthenticationToken(
    val clientId: String? = null,
    val signature: String? = null,
    val payload: String? = null,
    private val authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : Authentication {

    private var principal = clientId
    private var isAuthenticated = false

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

    override fun getName(): String {
        return this.clientId!!
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return this.authorities
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return principal
    }

    override fun isAuthenticated(): Boolean {
        return this.isAuthenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.isAuthenticated = isAuthenticated
    }
}