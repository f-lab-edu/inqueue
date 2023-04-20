package com.flab.inqueue.domain.member.exception

class MemberNotFoundException(
    clientId: String
) : RuntimeException("Customer(clientId=${clientId}) not Found")