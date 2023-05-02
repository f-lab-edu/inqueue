package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.queue.dto.JobResponse
import com.flab.inqueue.domain.queue.dto.JobInfo
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.WaitQueueRedisRepository
import org.springframework.stereotype.Service

@Service
class WaitQueueService(
    private val waitQueueRedisRepository: WaitQueueRedisRepository,
) {
    fun register(job: Job): JobResponse {
        waitQueueRedisRepository.register(job)
        return retrieve(job)
    }

    fun isMember(job: Job): Boolean {
        return waitQueueRedisRepository.isMember(job)
    }

    fun retrieve(job: Job): JobResponse {
        val rank = (waitQueueRedisRepository.rank(job)) + 1
        val waitSecond = rank * job.waitTimePerOneJob
        return JobResponse(JobStatus.WAIT, JobInfo(waitSecond, rank.toInt()))
    }

    fun size(key: String): Long {
        return waitQueueRedisRepository.size(key)
    }

    fun remove(job: Job) {
        waitQueueRedisRepository.remove(job)
    }
}