package com.flab.inqueue.domain.queue.entity

class Job(
    val eventId: String,
    val userId: String,
    var status: JobStatus = JobStatus.WAIT,
    val workingTimeSec: Long = 1L
) {

    val redisKey = status.makeRedisKey(eventId)
    val redisValue = "${redisKey}:${userId}"

}
