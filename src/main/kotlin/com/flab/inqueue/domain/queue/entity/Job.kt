package com.flab.inqueue.domain.queue.entity

class Job(
    val eventId: String,
    val userId: String,
    var status: JobStatus = JobStatus.WAIT,
    val jobQueueLimitTime: Long = 1L,
    val jobQueueSize: Long? = null
) {
    val redisKey = status.makeRedisKey(eventId)
    val redisValue = "${redisKey}:${userId}"

    val waitTimePerOneJob: Long
        get() = if (jobQueueSize == null) 0L else jobQueueLimitTime / jobQueueSize
}

