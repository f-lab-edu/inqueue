package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.exception.RedisDataAccessException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Repository
class JobRedisRepository(
    private val jobRedisTemplate: RedisTemplate<String, Job>,
    private val userRedisTemplate: RedisTemplate<String, String>,
) {

    @Transactional
    fun register(job: Job) {
        jobRedisTemplate.opsForSet().add(job.redisKey, job)
        userRedisTemplate.opsForValue().set(job.redisValue, job.redisValue, job.queueLimitTime, TimeUnit.SECONDS)
    }

    @Transactional
    fun remove(job: Job) {
        jobRedisTemplate.opsForSet().remove(job.redisKey, job) ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
        userRedisTemplate.opsForValue().getAndDelete(job.redisValue)
            ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
    }

    fun size(key: String): Long {
        return jobRedisTemplate.opsForSet().size(key) ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
    }

    @Transactional
    fun isMember(job: Job): Boolean {
        val hasUser = userRedisTemplate.opsForValue().get(job.redisValue)
        if (hasUser != null) {
            return true
        }
        userRedisTemplate.opsForValue().getAndDelete(job.redisValue)
            ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
        jobRedisTemplate.opsForSet().remove(job.redisKey, job) ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
        return false
    }
}