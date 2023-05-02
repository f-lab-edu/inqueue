package com.flab.inqueue.domain.queue.entity

import java.time.Instant

class Job(
    val eventId: String,
    val userId: String,
    var status: JobStatus = JobStatus.WAIT,
    val workingTimeSec : Long = 1L
) {

    private val redisKey =  status.makeRedisKey(this.eventId)
    fun redisKey(): String = redisKey
    fun redisValue() : String = StringBuilder(redisKey).append(":").append(userId).toString()
}
