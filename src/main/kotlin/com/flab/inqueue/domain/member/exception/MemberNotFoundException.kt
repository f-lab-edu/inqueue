package com.flab.inqueue.domain.member.exception

class MemberNotFoundException(message: String) : MemberException(404, message)