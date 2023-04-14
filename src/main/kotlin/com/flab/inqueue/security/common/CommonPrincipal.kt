package com.flab.inqueue.security.common

data class CommonPrincipal(
    val clientId: String,
    val userId: String? = null,
    val roles: List<Role>
)
