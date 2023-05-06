package com.flab.inqueue.domain.queue.repository

import com.flab.inqueue.TestContainer
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.support.RedisConfigTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import java.util.*

@ComponentScan(basePackages = ["com.flab.inqueue.domain.queue.repository"])
@Import(RedisConfigTest::class)
@DataRedisTest
class WaitQueueRedisRepositoryTest : TestContainer() {

    @Autowired
    private lateinit var waitQueueRedisRepository: WaitQueueRedisRepository
    @Autowired
    private lateinit var waitQueueRedisTemplate: RedisTemplate<String, Job>
    @Autowired
    private lateinit var userRedisTemplate: RedisTemplate<String, String>

    @Test
    fun popMin() {
        // given
        val eventId = UUID.randomUUID().toString()
        val size = 10L
        (0 until size).forEach { i ->
            waitQueueRedisRepository.register(Job(eventId, "user$i"))
        }

        // when
        val jobs = waitQueueRedisRepository.popMin(JobStatus.WAIT.makeRedisKey(eventId), size).map { it.value!! }

        // then
        assertThat(jobs.size).isEqualTo(size)
        assertThat(waitQueueRedisTemplate.opsForZSet().size(JobStatus.WAIT.makeRedisKey(eventId))).isEqualTo(0L)
        jobs.forEach {
                job -> assertThat(userRedisTemplate.opsForValue().get(job.redisValue)).isNull()
        }
    }
}
