package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository

@Repository
class WaitQueueRedisRepository(
    private val jobRedisTemplate: RedisTemplate<String, Job>,
) {
    fun register(job: Job) {
        jobRedisTemplate.opsForZSet().add(job.redisKey(), job, System.nanoTime().toDouble())
    }

    fun size(key: String): Long? {
        return jobRedisTemplate.opsForZSet().size(key)
    }

    fun range(key: String, start: Long, end: Long): MutableSet<Job>? {
        return jobRedisTemplate.opsForZSet().range(key, start, end)
    }

    fun deleteRange(key: String, start: Long, end: Long): Long? {
        return jobRedisTemplate.opsForZSet().removeRange(key, start, end)
    }

    fun rank(job: Job): Long? {
        return jobRedisTemplate.opsForZSet()
            .rank(job.redisKey(), job)
    }

    fun remove(job: Job): Long? {
        return jobRedisTemplate.opsForZSet().remove(job.redisKey(), job)
    }

    fun findMe(job: Job): Cursor<ZSetOperations.TypedTuple<Job>> {
        return jobRedisTemplate.opsForZSet().scan(job.redisKey(), ScanOptions.NONE)
    }

}