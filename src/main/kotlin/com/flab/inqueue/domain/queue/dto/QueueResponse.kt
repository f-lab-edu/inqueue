package com.flab.inqueue.domain.queue.dto

import com.flab.inqueue.domain.queue.entity.JobStatus
import java.time.LocalTime

data class QueueResponse(
    val status: JobStatus,
    val expectedInfo: QueueInfo? = null,
)

data class QueueInfo(val time: LocalTime, var order: Int)
