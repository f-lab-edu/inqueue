package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.member.dto.MemberSignUpRequest
import com.flab.inqueue.domain.member.dto.MemberSignUpResponse
import com.flab.inqueue.domain.member.service.MemberService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/server/v1/members")
class MemberController(
    private val memberService: MemberService
) {

    @PostMapping
    fun signUp(@RequestBody request: MemberSignUpRequest): MemberSignUpResponse {
        return memberService.signUp(request)
    }
}