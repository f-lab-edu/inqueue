package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Repository
class UserRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) {

    @Transactional
    fun register(job: Job) {
        redisTemplate.opsForSet().add(job.redisKey(), job.redisValue() )
        job.redisKeySecTTL?.let { redisTemplate.expireAt(job.redisKey(), it) }
        redisTemplate.opsForSet().add(job.redisValue() ,job.redisValue() )
        // TODO:대기열, 작업엽 검증에 있어서 TTL 이 달라야 할 것 같습니다.
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
        if(isRedisValue) {
            // todo job.redisValue() ttl 업데이트 -> TTL 업데이트 해주고 조회했을때 살아있으면
            return true
        }
        // todo TTL 없으면 job.redisKey(), job.redisValue() 제거
        redisTemplate.opsForSet().remove(job.redisKey(), job.redisValue())
        return false;
    }
}