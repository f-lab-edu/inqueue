package com.flab.inqueue.domain.member.exception

abstract class MemberException(val statusCode: Int = 400, message: String) : RuntimeException(message)