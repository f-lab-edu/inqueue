package com.flab.inqueue.domain.customer.repository

import com.flab.inqueue.domain.customer.entity.Customer
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {

    @EntityGraph(attributePaths = ["roles"])
    fun findByClientId(clientId: String): Customer?

}