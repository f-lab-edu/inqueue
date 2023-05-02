package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Repository
class JobRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    @Transactional
    fun register(job: Job) {
        redisTemplate.opsForSet().add(job.redisKey(), job.redisValue())
        redisTemplate.opsForValue().set(job.redisValue(), job.redisValue(), job.workingTimeSec, TimeUnit.SECONDS)
    }

    @Transactional
    fun remove(job: Job): Boolean {
        redisTemplate.opsForSet().remove(job.redisKey(), job.redisValue())
        redisTemplate.opsForValue().getAndDelete(job.redisValue())
        return true
    }

    fun size(key: String): Long? {
        return redisTemplate.opsForSet().size(key)
    }

    @Transactional
    fun isMember(job: Job): Boolean {
        val isRedisValue = redisTemplate.opsForValue().get(job.redisValue())
        if (isRedisValue != null) {
            redisTemplate.opsForValue().set(job.redisValue(), job.redisValue(), job.workingTimeSec, TimeUnit.SECONDS)
            return true
        }
        redisTemplate.opsForValue().getAndDelete(job.redisValue())
        redisTemplate.opsForSet().remove(job.redisKey(), job.redisValue())
        return false
    }
}