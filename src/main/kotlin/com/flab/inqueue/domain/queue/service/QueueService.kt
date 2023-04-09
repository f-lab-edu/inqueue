package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.queue.entity.Work
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class QueueService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {

    fun register(work: Work): Long? {
        redisTemplate.opsForZSet().add(work.eventId, work, System.nanoTime().toDouble())
        return getRedisSize(work.eventId)
    }

    fun registerAll(work: Work): Long? {
        redisTemplate.opsForZSet().add(work.eventId, work, System.nanoTime().toDouble())
        return getRedisSize(work.eventId)
    }

    fun getRedisSize(key: String): Long? {
        return redisTemplate.opsForZSet().size(key);
    }

    fun getRank(work: Work): Long? {
        return redisTemplate.opsForZSet().rank(work.eventId, work)
    }

}