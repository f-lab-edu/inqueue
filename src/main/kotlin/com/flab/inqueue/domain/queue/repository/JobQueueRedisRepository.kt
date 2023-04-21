package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class JobQueueRedisRepository(
    private val jobRedisTemplate: RedisTemplate<String, Job>,
) {
    fun register(job: Job) {
        jobRedisTemplate.opsForSet().add(job.redisKey(), job)
    }

    fun remove(job: Job): Long? {
        return jobRedisTemplate.opsForZSet().remove(job.redisKey(), job)
    }

    fun size(key: String): Long? {
        return jobRedisTemplate.opsForZSet().size(key)
    }

    fun isMember(job: Job): Boolean? {
        return jobRedisTemplate.opsForSet().isMember(job.redisKey(), job)
    }
}