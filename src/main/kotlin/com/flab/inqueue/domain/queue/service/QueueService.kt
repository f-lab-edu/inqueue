package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.queue.dto.QueueInfo
import com.flab.inqueue.domain.queue.dto.QueueResponse
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.QueueRedisPository
import org.springframework.stereotype.Service

@Service
class QueueService(
    private val queueRedisPository: QueueRedisPository
) {

    fun register(job: Job): QueueResponse {
        queueRedisPository.register(job)
        if( job.status == JobStatus.ENTER){
            return QueueResponse(job.status)
        }
        return retrieve(job)
    }

    fun size(key: String): Long? {
        return queueRedisPository.size(key)
    }

    fun retrieve(job: Job): QueueResponse {
        val rank = (queueRedisPository.rank(job) ?: 0) + 1
        val waitSecond = rank * 10
        return QueueResponse( JobStatus.WAIT, QueueInfo(waitSecond, rank.toInt()))
    }

    fun range(key: String, start: Long, end: Long): MutableSet<Job>? {
        return queueRedisPository.range(key,start,end)
    }
    fun deleteRange(key: String, start: Long, end: Long): Long? {
        return queueRedisPository.deleteRange(key,start,end)
    }

}