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
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ComponentScan(basePackages = ["com.flab.inqueue.domain.queue.repository"])
@Import(RedisConfigTest::class)
@ActiveProfiles("test")
@DataRedisTest
class JobRedisRepositoryTest : TestContainer() {

    @Autowired
    private lateinit var jobRedisRepository: JobRedisRepository

    @Autowired
    private lateinit var jobRedisTemplate: RedisTemplate<String, Job>

    @Autowired
    private lateinit var userRedisTemplate: RedisTemplate<String, String>

    @Test
    fun registerAll() {
        // given
        val eventId = UUID.randomUUID().toString()
        val size = 10L
        val jobs = mutableListOf<Job>()
        (0 until size).forEach { i ->
            jobs.add(Job(eventId, "user$i", JobStatus.ENTER, 10L))
        }

        // when
        jobRedisRepository.registerAll(jobs)

        // then
        assertThat(jobRedisTemplate.opsForSet().size(JobStatus.ENTER.makeRedisKey(eventId))).isEqualTo(size)
        jobs.forEach {
                job -> assertThat(userRedisTemplate.opsForValue().get(job.redisValue)).isNotNull()
        }
    }
}
