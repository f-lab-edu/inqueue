package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.domain.customer.repository.CustomerRepository
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class HmacAuthenticationProvider(
    private val hmacSignatureVerifier: HmacSignatureVerifier,
    private val customerRepository: CustomerRepository
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication {
        val hmacAuthentication = authentication as HmacAuthenticationToken
        val customer = customerRepository.findByClientId(authentication.clientId!!)
            ?: throw UsernameNotFoundException("Customer(clientId=${authentication.clientId}) not found")

        val isValid = hmacSignatureVerifier.verify(
            signature = hmacAuthentication.signature!!,
            clientSecret = customer.clientSecret,
            payload = hmacAuthentication.payload!!
        )

        if (!isValid) {
            throw BadCredentialsException("Invalid hmac authentication")
        }

        return HmacAuthenticationToken.authenticatedToken(
            clientId = customer.clientId,
            authorities = customer.roles.map { SimpleGrantedAuthority("ROLE_$it") }.toMutableList()
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return HmacAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}