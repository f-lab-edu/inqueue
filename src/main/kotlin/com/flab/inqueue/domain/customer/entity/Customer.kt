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
    val roles: List<Role>
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    var name: String = name
        private set

    val createdAt: LocalDateTime = LocalDateTime.now()

    companion object {

        @JvmStatic
        fun admin(name: String, clientId: String, clientSecret: String): Customer {
            return Customer(name, clientId, clientSecret, listOf(Role.USER, Role.ADMIN))
        }

        @JvmStatic
        fun user(name: String, clientId: String, clientSecret: String): Customer {
            return Customer(name, clientId, clientSecret, listOf(Role.USER))
        }
    }
}