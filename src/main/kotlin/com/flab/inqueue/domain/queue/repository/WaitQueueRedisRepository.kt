package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.exception.RedisDataAccessException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class WaitQueueRedisRepository(
    @Qualifier("jobRedisTemplate")
    private val waitQueueRedisTemplate: RedisTemplate<String, Job>,
    private val userRedisTemplate: RedisTemplate<String, String>,
) {
    fun register(job: Job): Long {
        waitQueueRedisTemplate.opsForZSet().add(job.redisKey, job, System.nanoTime().toDouble())
        userRedisTemplate.opsForValue().set(job.redisValue, job.redisValue, job.queueLimitTime, TimeUnit.SECONDS)
        return rank(job)
    }

    fun size(key: String): Long {
        return waitQueueRedisTemplate.opsForZSet().size(key) ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
    }

    fun range(key: String, start: Long, end: Long): MutableSet<Job> {
        return waitQueueRedisTemplate.opsForZSet().range(key, start, end)
            ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
    }

    fun rank(job: Job): Long {
        return waitQueueRedisTemplate.opsForZSet().rank(job.redisKey, job)
            ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
    }

    fun remove(job: Job) {
        waitQueueRedisTemplate.opsForZSet().remove(job.redisKey, job)
            ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
        userRedisTemplate.opsForValue().getAndDelete(job.redisValue)
            ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
    }

    fun findMe(job: Job): Cursor<ZSetOperations.TypedTuple<Job>> {
        return waitQueueRedisTemplate.opsForZSet().scan(job.redisKey, ScanOptions.NONE)
    }

    fun isMember(job: Job): Boolean {
        val hasUser = userRedisTemplate.opsForValue().get(job.redisValue)
        return hasUser != null
    }

    fun updateUserTtl(job: Job) {
        userRedisTemplate.opsForValue().set(job.redisValue,job.redisValue, job.queueLimitTime, TimeUnit.SECONDS)
    }
}