package com.flab.inqueue.security

import com.flab.inqueue.domain.customer.repository.CustomerRepository
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomerUserDetailsService(
    val customerRepository: CustomerRepository
) : UserDetailsService {

    override fun loadUserByUsername(clientId: String): UserDetails {
        val customer = customerRepository.findByClientId(clientId)
            ?: throw BadCredentialsException("Customer(clientId=${clientId}) not found")
        return User(customer.clientId, customer.clientSecret, emptyList())
    }
}