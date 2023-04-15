package com.flab.inqueue.security.common

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority


open class CommonAuthentication(
    private val name: String?,
    private val principal: CommonPrincipal?,
    private var isAuthenticated: Boolean,
    private val authorities: MutableCollection<out GrantedAuthority> = mutableListOf()
) : Authentication {

    override fun getName(): String? {
        return name
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

    override fun getPrincipal(): CommonPrincipal? {
        return this.principal
    }

    override fun isAuthenticated(): Boolean {
        return isAuthenticated
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.isAuthenticated = isAuthenticated
    }
}