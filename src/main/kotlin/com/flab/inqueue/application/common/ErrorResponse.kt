package com.flab.inqueue.application.common

import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val error: String,
    val status: Int,
    val path: String
)