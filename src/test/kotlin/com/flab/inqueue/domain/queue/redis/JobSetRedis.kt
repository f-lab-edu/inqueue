package com.flab.inqueue.domain.queue.redis

import com.flab.inqueue.TestContainer
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.JobRedisRepository
import com.flab.inqueue.support.RedisConfigTest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import java.util.*


@DataRedisTest
@Import(RedisConfigTest::class)
@ComponentScan(basePackages = ["com.flab.inqueue.domain.queue.repository"])
class JobSetRedis @Autowired constructor(
    private val jobRedisRepository: JobRedisRepository,
    private val jobRedisTemplate: RedisTemplate<String, Job>,
    private val userRedisTemplate: RedisTemplate<String, String>
) : TestContainer() {

    private val logger = LoggerFactory.getLogger(JobSetRedis::class.java)

    @Test
    @DisplayName("redis set ttl 테스트")
    fun testRedisTTL() {
        val job = Job("testEventId", UUID.randomUUID().toString(), JobStatus.ENTER, 1L)

        val startTime = System.currentTimeMillis()
        jobRedisRepository.register(job)
        logger.info("jobSetRedisRepository 저장 걸린시간 : {} ms ", System.currentTimeMillis() - startTime)

        val existExpire1 = jobRedisTemplate.getExpire(job.redisKey)
        val existExpire2 = userRedisTemplate.getExpire(job.redisValue)

        logger.info("check existExpire  : job.redisKey() = {}  , job.redisValue() = {} ", existExpire1, existExpire2)

        Thread.sleep(2000L)

        val expire1 = jobRedisTemplate.getExpire(job.redisKey)
        val expire2 = userRedisTemplate.getExpire(job.redisValue)

        logger.info("check expire  : job.redisKey() = {}  , job.redisValue() = {} ", expire1, expire2)
    }
}