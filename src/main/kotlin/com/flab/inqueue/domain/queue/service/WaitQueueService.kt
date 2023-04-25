package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.queue.dto.QueueInfo
import com.flab.inqueue.domain.queue.dto.QueueResponse
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.JobQueueRedisRepository
import com.flab.inqueue.domain.queue.repository.WaitQueueRedisRepository
import org.springframework.stereotype.Service

@Service
class WaitQueueService(
    private val waitQueueRedisRepository: WaitQueueRedisRepository,
    private val jobQueueRedisRepository: JobQueueRedisRepository,
) {

    fun waitQueueRegister(job: Job): QueueResponse {
        jobQueueRedisRepository.register(job)
        if (job.status == JobStatus.ENTER) {
            return QueueResponse(job.status)
        }
        waitQueueRedisRepository.register(job)
        return waitQueueRetrieve(job)
    }

    fun waitQueueRetrieve(job: Job): QueueResponse {
        if (!isMember(job)) return QueueResponse(JobStatus.TIMEOUT)
        val rank = (waitQueueRedisRepository.rank(job) ?: 0) + 1
        val waitSecond = rank * 10
        return QueueResponse(JobStatus.WAIT, QueueInfo(waitSecond, rank.toInt()))
    }

    fun size(key: String): Long? {
        return waitQueueRedisRepository.size(key)
    }

    fun isMember(job: Job): Boolean {
        return jobQueueRedisRepository.isMember(job)
    }

    fun range(key: String, start: Long, end: Long): MutableSet<Job>? {
        return waitQueueRedisRepository.range(key, start, end)
    }

    fun deleteRange(key: String, start: Long, end: Long): Long? {
        return waitQueueRedisRepository.deleteRange(key, start, end)
    }

}