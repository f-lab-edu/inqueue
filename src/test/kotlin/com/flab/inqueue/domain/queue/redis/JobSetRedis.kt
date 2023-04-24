package com.flab.inqueue.domain.queue.redis

import com.flab.inqueue.TestContainer
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.JobQueueRedisRepository
import com.flab.inqueue.domain.queue.repository.WaitQueueRedisRepository
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
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*



@DataRedisTest
@Import(RedisConfigTest::class)
@ComponentScan(basePackages = ["com.flab.inqueue.domain.queue.repository"])
class JobSetRedis @Autowired constructor(
    private val waitQueueRedisRepository: WaitQueueRedisRepository,
    private val jobQueueRedisRepository: JobQueueRedisRepository,
    private val redistemplate: RedisTemplate<String, String>,

    ) : TestContainer() {

    private val logger = LoggerFactory.getLogger(JobSetRedis::class.java)

    @Test
    @DisplayName("redis set ttl 테스트")
    fun testRedisTTL() {
        val expireInstant = LocalDateTime.now().plusSeconds(1).toInstant(ZoneOffset.ofHours(9))
        val job = Job("testEventId", UUID.randomUUID().toString(), JobStatus.ENTER,expireInstant)

        val startTime = System.currentTimeMillis()
        jobQueueRedisRepository.register(job)
        logger.info("jobSetRedisRepository 저장 걸린시간 : {} ms ", System.currentTimeMillis() - startTime)

        val existExpire1 = redistemplate.getExpire(job.redisKey())
        val existExpire2 = redistemplate.getExpire(job.redisValue())

        logger.info("chech existExpire  : job.redisKey() = {}  , job.redisValue() = {} ", existExpire1, existExpire2)

        Thread.sleep(2000L)

        val expire1 = redistemplate.getExpire(job.redisKey())
        val expire2 = redistemplate.getExpire(job.redisValue())

        logger.info("chech expire  : job.redisKey() = {}  , job.redisValue() = {} ", expire1, expire2)
    }

}