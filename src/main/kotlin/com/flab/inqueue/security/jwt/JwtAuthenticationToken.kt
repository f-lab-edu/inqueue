package com.flab.inqueue.security.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken(
    private val userId: String? = null,
    val jwtToken: String? = null,
    private val authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : Authentication {

    private var isAuthenticated = false

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

    override fun getName(): String? {
        return this.userId
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getDetails(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return userId
    }

    override fun isAuthenticated(): Boolean {
        return this.isAuthenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.isAuthenticated = isAuthenticated
    }
}