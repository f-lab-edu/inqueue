package com.flab.inqueue.domain.queue.dto

import com.flab.inqueue.domain.queue.entity.JobStatus

data class JobResponse(
    val status: JobStatus,
    val expectedInfo: JobInfo? = null,
)

data class JobInfo(val second: Long, var order: Int)
