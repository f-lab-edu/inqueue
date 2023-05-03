package com.flab.inqueue.domain.member.dto

data class MemberSignUpRequest(
    val name: String,
    val phone: String? = null
)
