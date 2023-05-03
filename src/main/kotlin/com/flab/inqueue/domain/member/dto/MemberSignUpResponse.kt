package com.flab.inqueue.domain.member.dto

import com.flab.inqueue.domain.member.entity.MemberKey

data class MemberSignUpResponse(
    val name: String,
    val key: MemberKey
)
