package com.flab.inqueue.domain.queue.entity

class Job(
    val eventId: String,
    val userId: String,
    var status: JobStatus = JobStatus.WAIT,
) {
    fun redisKey(): String = status.makeRedisKey(this.eventId)
}
