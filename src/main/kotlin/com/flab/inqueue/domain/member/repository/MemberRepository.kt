package com.flab.inqueue.domain.member.repository

import com.flab.inqueue.domain.member.entity.Member
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = ["roles"])
    fun findByKeyClientId(clientId: String): Member?

}