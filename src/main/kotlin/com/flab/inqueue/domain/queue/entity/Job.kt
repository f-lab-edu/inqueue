package com.flab.inqueue.domain.queue.entity

class Job(
    val eventId: String,
    val userId: String,
    var status: JobStatus = JobStatus.WAIT,
    val queueLimitTime: Long = 1L, // 기본값을 대기열의 TTL 로 한다, 작업열의 경우 해당 값을 설정 해주어야 한다.
    val jobQueueSize: Long? = null
) {
    val redisKey: String
        get() = status.makeRedisKey(eventId)
    val redisValue: String
        get() = "${redisKey}:${userId}"
    val waitTimePerOneJob: Long
        get() = if (jobQueueSize == null) 0L else queueLimitTime / jobQueueSize

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Job

        if (eventId != other.eventId) return false
        if (userId != other.userId) return false
        return status == other.status
    }

    override fun hashCode(): Int {
        var result = eventId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}

