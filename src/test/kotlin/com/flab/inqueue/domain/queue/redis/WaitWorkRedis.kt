package com.flab.inqueue.domain.queue.redis

import com.flab.inqueue.createEventRequest
import com.flab.inqueue.domain.queue.entity.Work
import com.flab.inqueue.support.EmbeddedRedisConfig
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import java.util.*


@DataRedisTest
@ActiveProfiles("test")
@Import(EmbeddedRedisConfig::class)
class WaitWorkRedis @Autowired constructor(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private val logger = LoggerFactory.getLogger(WaitWorkRedis::class.java)



    @Test
    fun `등록`() {
        //given
        val event = createEventRequest().toEntity()
        val work = Work(event.eventId, UUID.randomUUID().toString().take(10))
        redisTemplate.opsForZSet().add(event.eventId, work, System.nanoTime().toDouble())
        //when
        val range = redisTemplate.opsForZSet().range(event.eventId, 0, -1)
        //then
        assertThat(range?.size).isEqualTo(1)
    }

    @Test
    fun `key의 전체 사이즈(갯수) 조회`() {
        //given
        val event = createEventRequest().toEntity()
        redisTemplate.opsForZSet().add(
            event.eventId, Work(event.eventId, UUID.randomUUID().toString().take(10)), System.nanoTime().toDouble()
        )
        redisTemplate.opsForZSet().add(
            event.eventId, Work(event.eventId, UUID.randomUUID().toString().take(10)), System.nanoTime().toDouble()
        )
        redisTemplate.opsForZSet().add(
            event.eventId, Work(event.eventId, UUID.randomUUID().toString().take(10)), System.nanoTime().toDouble()
        )
        //when
        val long = redisTemplate.opsForZSet().size(event.eventId)
        logger.info("{}", long)
        //then
        assertThat(long).isEqualTo(3)
    }

    @Test
    fun `key의 rank 조회`() {
        //given
        val event = createEventRequest().toEntity()
        val work1 = Work(event.eventId, UUID.randomUUID().toString().take(10))
        val work2 = Work(event.eventId, UUID.randomUUID().toString().take(10))
        val work3 = Work(event.eventId, UUID.randomUUID().toString().take(10))
        redisTemplate.opsForZSet().add(event.eventId, work1, System.nanoTime().toDouble())
        redisTemplate.opsForZSet().add(event.eventId, work2, System.nanoTime().toDouble())
        redisTemplate.opsForZSet().add(event.eventId, work3, System.nanoTime().toDouble())


        //when
        val totalSize = redisTemplate.opsForZSet().size(event.eventId)
        val rank1 = redisTemplate.opsForZSet().rank(event.eventId, work1)
        val rank2 = redisTemplate.opsForZSet().rank(event.eventId, work2)
        val rank3 = redisTemplate.opsForZSet().rank(event.eventId, work3)

        //then
        assertThat(rank1).isEqualTo(0)
        assertThat(totalSize).isEqualTo(3)
    }


    @Test
    fun `key의 rank 삭제 `() {
        //given
        val event = createEventRequest().toEntity()
        val work1 = Work(event.eventId, UUID.randomUUID().toString().take(10))
        val work2 = Work(event.eventId, UUID.randomUUID().toString().take(10))
        val work3 = Work(event.eventId, UUID.randomUUID().toString().take(10))
        redisTemplate.opsForZSet().add(event.eventId, work1, System.nanoTime().toDouble())
        redisTemplate.opsForZSet().add(event.eventId, work2, System.nanoTime().toDouble())
        redisTemplate.opsForZSet().add(event.eventId, work3, System.nanoTime().toDouble())


        //when
        val remove = redisTemplate.opsForZSet().remove(event.eventId, work1)
        val totalSize = redisTemplate.opsForZSet().size(event.eventId)

        //then
        assertThat(remove).isNotNull
        assertThat(totalSize).isEqualTo(2)
    }

    @Test
    fun `key의 범위 조회`() {
        //given
        val event = createEventRequest().toEntity()
        repeat(100) {
            redisTemplate.opsForZSet().add(
                event.eventId, Work(event.eventId, UUID.randomUUID().toString().take(10)), System.nanoTime().toDouble()
            )
        }

        //when
        val range = redisTemplate.opsForZSet().range(event.eventId, 0, 9)

        //then
        assertThat(range?.size).isEqualTo(10)
    }

    @Test
    fun `key의 범위 삭제`() {
        //given
        val event = createEventRequest().toEntity()
        repeat(100) {
            redisTemplate.opsForZSet().add(
                event.eventId, Work(event.eventId, UUID.randomUUID().toString().take(10)), System.nanoTime().toDouble()
            )
        }

        //when
        val range = redisTemplate.opsForZSet().removeRange(event.eventId, 0, 9)
        val size = redisTemplate.opsForZSet().size(event.eventId)
        //then
        assertThat(size).isEqualTo(90)
    }
}