package com.flab.inqueue.domain.dto

import java.time.LocalTime

data class QueueResponse(
    val status: String,
    val expectedInfo: QueueInfo?,
)

data class QueueInfo(val time: LocalTime, var order: Int)
