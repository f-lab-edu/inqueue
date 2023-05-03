package com.flab.inqueue.domain.member.entity

import com.flab.inqueue.common.domain.BaseEntity
import com.flab.inqueue.security.common.Role
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "MEMBER")
class Member(
    var name: String,
    var phone: String? = null,
    @Embedded
    val key: MemberKey,
    @ElementCollection
    @CollectionTable(
        name = "MEMBER_ROLE",
        joinColumns = [JoinColumn(name = "member_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val roles: List<Role> = listOf(Role.USER),
    val createdDateTime: LocalDateTime = LocalDateTime.now()
) : BaseEntity()