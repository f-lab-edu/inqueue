package com.flab.inqueue.domain.customer.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Customer(
    name: String,
    val clientId: String,
    val clientSecret: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    var name: String = name
        private set

    private var createdAt: LocalDateTime? = null

    init {
        createdAt = LocalDateTime.now()
    }
}