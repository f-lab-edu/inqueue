package com.flab.inqueue.domain.queue.dto

import com.flab.inqueue.domain.queue.entity.JobStatus

data class QueueResponse(
    val status: JobStatus,
    val expectedInfo: QueueInfo? = null,
)

data class QueueInfo(val time: Long, var order: Int)
