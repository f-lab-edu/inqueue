package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Repository
class JobQueueRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    @Transactional
    fun register(job: Job) {
        redisTemplate.opsForSet().add(job.redisKey(), job.redisValue() )
        job.redisKeySecTTL?.let { redisTemplate.expireAt(job.redisKey(), it) }
        redisTemplate.opsForSet().add(job.redisValue() ,job.redisValue() )
        redisTemplate.expire(job.redisValue(), 1L, TimeUnit.SECONDS)
    }

    @Transactional
    fun remove(job: Job): Boolean {
        redisTemplate.opsForSet().remove(job.redisValue(), job.redisValue()) ?: return false
        redisTemplate.opsForSet().remove(job.redisKey(), job.redisValue()) ?: return false
        return true
    }
    fun size(key: String): Long? {
        return redisTemplate.opsForSet().size(key);
    }

    @Transactional
    fun isMember(job: Job): Boolean {
        val isRedisValue = redisTemplate.opsForSet().isMember(job.redisValue(), job.redisValue()) ?: throw NoSuchElementException("데이터가 존재하지 않습니다.")
        if(isRedisValue) return true
        redisTemplate.opsForSet().remove(job.redisKey(), job.redisValue())
        return false;
    }
}