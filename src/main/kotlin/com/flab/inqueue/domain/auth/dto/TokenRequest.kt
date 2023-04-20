package com.flab.inqueue.domain.auth.dto


data class TokenRequest(
    val clientId: String? = null,
    var userId: String
)

