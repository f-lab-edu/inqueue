package com.flab.inqueue.domain.member.entity

import com.flab.inqueue.common.domain.BaseEntity
import com.flab.inqueue.security.common.Role
import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "MEMBER")
class Member(
    var name: String,
    @Embedded
    val key: MemberKey,
    @ElementCollection
    @CollectionTable(
        name = "MEMBER_ROLE",
        joinColumns = [JoinColumn(name = "member_id")]
    )
    @Column(name = "role")
    val roles: List<Role> = listOf(Role.USER)
) : BaseEntity() {
    val createdAt: LocalDateTime = LocalDateTime.now()

    fun encryptMemberKey(encryptionUtil: EncryptionUtil) {
        key.encryptClientSecret(encryptionUtil)
    }
}