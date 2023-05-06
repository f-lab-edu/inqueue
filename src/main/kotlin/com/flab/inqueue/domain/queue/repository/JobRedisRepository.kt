package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.exception.RedisDataAccessException
import org.springframework.data.redis.connection.RedisStringCommands
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.types.Expiration
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class JobRedisRepository(
    private val jobRedisTemplate: RedisTemplate<String, Job>,
    private val userRedisTemplate: RedisTemplate<String, String>,
) {

    fun register(job: Job) {
        jobRedisTemplate.opsForSet().add(job.redisKey, job)
        userRedisTemplate.opsForValue().set(job.redisValue, job.redisValue, job.queueLimitTime, TimeUnit.SECONDS)
    }

    fun registerAll(jobs: List<Job>) {
        val jobKeySerializer = jobRedisTemplate.keySerializer as StringRedisSerializer
        val jobValueSerializer = jobRedisTemplate.valueSerializer as Jackson2JsonRedisSerializer
        jobRedisTemplate.executePipelined { connection ->
            for (job in jobs) {
                connection.setCommands().sAdd(
                    jobKeySerializer.serialize(job.redisKey), jobValueSerializer.serialize(job)
                )
            }
            null
        }

        userRedisTemplate.executePipelined { connection ->
            val redisConnection = connection as StringRedisConnection
            for (job in jobs) {
                redisConnection.set(
                    job.redisValue,
                    job.redisValue,
                    Expiration.seconds(job.queueLimitTime),
                    RedisStringCommands.SetOption.UPSERT
                )
            }
            null
        }
    }

    fun remove(job: Job) {
        jobRedisTemplate.opsForSet().remove(job.redisKey, job) ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
        userRedisTemplate.opsForValue().getAndDelete(job.redisValue)
            ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
    }

    fun size(key: String): Long {
        return jobRedisTemplate.opsForSet().size(key) ?: throw RedisDataAccessException("데이터에 접근 할 수 없습니다.")
    }

    fun isMember(job: Job): Boolean {
        val hasUser = userRedisTemplate.opsForValue().get(job.redisValue)
        return hasUser != null
    }
}