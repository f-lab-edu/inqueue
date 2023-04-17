package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class QueueService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {

    fun register(work: Job): Long? {
        redisTemplate.opsForZSet().add(work.eventId, work, System.nanoTime().toDouble())
        return getRedisSize(work.eventId)
    }

    fun registerAll(work: Job): Long? {
        redisTemplate.opsForZSet().add(work.eventId, work, System.nanoTime().toDouble())
        return getRedisSize(work.eventId)
    }

    fun getRedisSize(key: String): Long? {
        return redisTemplate.opsForZSet().size(key);
    }

    fun getRank(job: Job): Long? {
        return redisTemplate.opsForZSet().rank(job.eventId, job)
    }

}