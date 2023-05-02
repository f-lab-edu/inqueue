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
        val rank = waitQueueRedisRepository.register(job) + 1
        return JobResponse(JobStatus.WAIT, JobInfo(rank * job.waitTimePerOneJob, rank.toInt()))
    }

    fun isMember(job: Job): Boolean {
        return waitQueueRedisRepository.isMember(job)
    }

    fun retrieve(job: Job): JobResponse {
        if (!waitQueueRedisRepository.isMember(job)) {
            return JobResponse(JobStatus.TIMEOUT)
        }

        val rank = (waitQueueRedisRepository.rank(job)) + 1
        return JobResponse(JobStatus.WAIT, JobInfo(rank * job.waitTimePerOneJob, rank.toInt()))
    }

    fun size(key: String): Long {
        return waitQueueRedisRepository.size(key)
    }

    fun remove(job: Job) {
        waitQueueRedisRepository.remove(job)
    }
}