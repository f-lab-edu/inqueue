package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.repository.QueueRedisPository
import org.springframework.stereotype.Service

@Service
class QueueService(
    private val queueRedisPository: QueueRedisPository
) {

    fun register(job: Job) {
        return queueRedisPository.register(job)
    }

    fun size(key: String): Long? {
        return queueRedisPository.size(key)
    }

    fun rank(job: Job): Long? {
        return queueRedisPository.rank(job)
    }
    fun range(key: String, start: Long, end: Long): MutableSet<Job>? {
        return queueRedisPository.range(key,start,end)
    }
    fun deleteRange(key: String, start: Long, end: Long): Long? {
        return queueRedisPository.deleteRange(key,start,end)
    }

}