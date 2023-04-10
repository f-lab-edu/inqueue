package com.flab.inqueue.security.jwt.utils

data class VerifyJwtResponse(
    val userId: String? = null,
    val isValid: Boolean,
    val throwable: Throwable? = null
)