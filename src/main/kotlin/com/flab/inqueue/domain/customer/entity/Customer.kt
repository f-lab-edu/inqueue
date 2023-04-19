package com.flab.inqueue.domain.customer.entity

import com.flab.inqueue.security.common.Role
import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "CUSTOMER")
class Customer(
    var name: String,
    @Embedded
    val key: CustomerKey,
    @ElementCollection
    @CollectionTable(
        name = "CUSTOMER_ROLE",
        joinColumns = [JoinColumn(name = "customer_id")]
    )
    @Column(name = "role")
    val roles: List<Role> = listOf(Role.USER)
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    val createdAt: LocalDateTime = LocalDateTime.now()

    fun encryptClientSecret(encryptionUtil: EncryptionUtil) {
        key.encryptClientSecret(encryptionUtil)
    }
}