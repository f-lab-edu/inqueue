package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import jakarta.transaction.Transactional
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class WaitQueueRedisRepository(
    private val waitQueueRedisTemplate: RedisTemplate<String, Job>,
    private val userRedisTemplate: RedisTemplate<String, String>,
) {
    @Transactional
    fun register(job: Job) {
        waitQueueRedisTemplate.opsForZSet().add(job.redisKey(), job, System.nanoTime().toDouble())
        userRedisTemplate.opsForValue().set(job.redisValue(), job.redisValue(), job.workingTimeSec, TimeUnit.SECONDS)
    }

    fun size(key: String): Long? {
        return waitQueueRedisTemplate.opsForZSet().size(key)
    }

    fun range(key: String, start: Long, end: Long): MutableSet<Job>? {
        return waitQueueRedisTemplate.opsForZSet().range(key, start, end)
    }

    fun rank(job: Job): Long? {
        return waitQueueRedisTemplate.opsForZSet().rank(job.redisKey(), job)
    }

    @Transactional
    fun remove(job: Job) {
        waitQueueRedisTemplate.opsForZSet().remove(job.redisKey(), job)
        userRedisTemplate.opsForValue().getAndDelete(job.redisValue())
    }

    fun findMe(job: Job): Cursor<ZSetOperations.TypedTuple<Job>> {
        return waitQueueRedisTemplate.opsForZSet().scan(job.redisKey(), ScanOptions.NONE)
    }
}