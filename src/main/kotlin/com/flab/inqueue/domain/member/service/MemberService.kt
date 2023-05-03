package com.flab.inqueue.domain.member.service

import com.flab.inqueue.domain.member.dto.MemberSignUpRequest
import com.flab.inqueue.domain.member.dto.MemberSignUpResponse
import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.repository.MemberRepository
import com.flab.inqueue.domain.member.utils.memberkeygenrator.MemberKeyGenerator
import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
    private val memberKeyGenerator: MemberKeyGenerator,
    private val encryptionUtil: EncryptionUtil
) {

    @Transactional
    fun signUp(request: MemberSignUpRequest): MemberSignUpResponse {
        val memberKey = memberKeyGenerator.generate()
        val encryptedMemberKey = memberKey.encrypt(encryptionUtil)
        val member = Member(request.name, request.phone, encryptedMemberKey)
        memberRepository.save(member)
        return MemberSignUpResponse(member.name, memberKey)
    }

}