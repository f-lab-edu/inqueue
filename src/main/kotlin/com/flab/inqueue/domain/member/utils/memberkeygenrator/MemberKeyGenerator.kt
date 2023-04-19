package com.flab.inqueue.domain.member.utils.memberkeygenrator

import com.flab.inqueue.domain.member.entity.MemberKey

class MemberKeyGenerator(
    val memberKeyGenerationStrategy: MemberKeyGenerateStrategy
) {
    fun generate(): MemberKey {
        return memberKeyGenerationStrategy.generate()
    }
}
