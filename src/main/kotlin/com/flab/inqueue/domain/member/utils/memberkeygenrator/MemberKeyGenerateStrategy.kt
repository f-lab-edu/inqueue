package com.flab.inqueue.domain.member.utils.memberkeygenrator

import com.flab.inqueue.domain.member.entity.MemberKey

interface MemberKeyGenerateStrategy {

    fun generate() : MemberKey

}