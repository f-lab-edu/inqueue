package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Repository
class JobRedisRepository(
    private val jobRedisTemplate: RedisTemplate<String, String>,
    private val userRedisTemplate: RedisTemplate<String, String>,
) {

    @Transactional
    fun register(job: Job) {
        jobRedisTemplate.opsForSet().add(job.redisKey, job.redisValue)
        userRedisTemplate.opsForValue().set(job.redisValue, job.redisValue, job.workingTimeSec, TimeUnit.SECONDS)
    }

    @Transactional
    fun remove(job: Job): Boolean {
        jobRedisTemplate.opsForSet().remove(job.redisKey, job.redisValue)
        userRedisTemplate.opsForValue().getAndDelete(job.redisValue)
        return true
    }

    fun size(key: String): Long? {
        return jobRedisTemplate.opsForSet().size(key)
    }

    @Transactional
    fun isMember(job: Job): Boolean {
        val isRedisValue = userRedisTemplate.opsForValue().get(job.redisValue)
        if (isRedisValue != null) {
            return true
        }
        userRedisTemplate.opsForValue().getAndDelete(job.redisValue)
        jobRedisTemplate.opsForSet().remove(job.redisKey, job.redisValue)
        return false
    }
}