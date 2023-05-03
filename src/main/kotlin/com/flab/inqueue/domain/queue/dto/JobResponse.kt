package com.flab.inqueue.domain.queue.dto

import com.flab.inqueue.domain.queue.entity.JobStatus

data class JobResponse(
    val status: JobStatus,
    val expectedInfo: WaitQueueInfo? = null,
)

data class WaitQueueInfo(val second: Long, var order: Int)
