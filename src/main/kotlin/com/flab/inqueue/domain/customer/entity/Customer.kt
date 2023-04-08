package com.flab.inqueue.domain.customer.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "CUSTOMER")
class Customer(
    name: String,
    val clientId: String,
    val clientSecret: String,

    @ElementCollection
    @CollectionTable(
        name = "CUSTOMER_ROLE",
        joinColumns = [JoinColumn(name = "customer_id")]
    )
    @Column(name = "role")
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