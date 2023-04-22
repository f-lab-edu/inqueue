package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class JobQueueRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    fun register(job: Job) {
        redisTemplate.opsForSet().add(job.redisKey(), job.redisValue() )
        job.redisKeySecTTL?.let { redisTemplate.expireAt(job.redisKey(), it) }
        redisTemplate.opsForSet().add(job.redisValue() ,job.redisValue() )
        redisTemplate.expire(job.redisValue(), 1L, TimeUnit.SECONDS)
    }

    fun remove(job: Job): Boolean {
        redisTemplate.opsForSet().remove(job.redisValue(), job.redisValue()) ?: return false
        redisTemplate.opsForSet().remove(job.redisKey(), job.redisValue()) ?: return false
        return true
    }
    fun size(key: String): Long? {
        return redisTemplate.opsForSet().size(key);
    }

    fun isMember(job: Job): Boolean {
        val isRedisValue = redisTemplate.opsForSet().isMember(job.redisValue(), job.redisValue())
        if(isRedisValue == true) {
            return true
        } else {
            redisTemplate.opsForSet().remove(job.redisKey(), job.redisValue())
            return false;
        }
    }
}