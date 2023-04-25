package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.repository.JobQueueRedisRepository
import io.lettuce.core.RedisException
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class JobQueueService(
    private val jobQueueRedisRepository: JobQueueRedisRepository,
) {

    fun size(key: String): Long {
        return jobQueueRedisRepository.size(key)
    }

    @Throws(RedisException::class)
    fun isMember(job: Job): Boolean {
        return jobQueueRedisRepository.isMember(job)
    }

    @Throws(RedisException::class)
    fun finish(job: Job) {
        jobQueueRedisRepository.remove(job)
    }
}