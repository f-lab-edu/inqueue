package com.flab.inqueue.domain.queue.redis

import com.flab.inqueue.TestContainer
import com.flab.inqueue.createEventRequest
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.support.RedisConfigTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.RedisTemplate
import java.util.*


@DataRedisTest
@Import(RedisConfigTest::class)
class WaitWorkRedis @Autowired constructor(
    private val redisTemplate: RedisTemplate<String, Any>,
) : TestContainer() {
    private val logger = LoggerFactory.getLogger(WaitWorkRedis::class.java)
    private val zSetOperations = redisTemplate.opsForZSet();

    @Test
    @DisplayName("레디스 실행 테스트")
    fun redisRunningTest() {
        assertTrue(redisContainer.isRunning);
    }

    @Test
    @DisplayName("등록")
    fun add() {
        //given
        val event = createEventRequest().toEntity()
        val job = Job(event.eventId, UUID.randomUUID().toString())
        zSetOperations.add(event.eventId, job, System.nanoTime().toDouble())
        //when
        val range = zSetOperations.range(event.eventId, 0, -1)
        //then
        assertThat(range?.size).isEqualTo(1)
    }

    @Test
    @DisplayName("key의 전체 사이즈(갯수) 조회")
    fun getKeySize() {
        //given
        val event = createEventRequest().toEntity()
        zSetOperations.add(
            event.eventId,Job(event.eventId, UUID.randomUUID().toString()), System.nanoTime().toDouble()
        )
        zSetOperations.add(
            event.eventId,Job(event.eventId, UUID.randomUUID().toString()), System.nanoTime().toDouble()
        )
        zSetOperations.add(
            event.eventId,Job(event.eventId, UUID.randomUUID().toString()), System.nanoTime().toDouble()
        )
        //when
        val long = zSetOperations.size(event.eventId)
        logger.info("{}", long)
        //then
        assertThat(long).isEqualTo(3)
    }

    @Test
    @DisplayName("key의 rank 조회")
    fun getRanksOfKey() {
        //given
        val event = createEventRequest().toEntity()
        val job1 =Job(event.eventId, UUID.randomUUID().toString())
        val job2 =Job(event.eventId, UUID.randomUUID().toString())
        val job3 =Job(event.eventId, UUID.randomUUID().toString())
        zSetOperations.add(event.eventId, job1, System.nanoTime().toDouble())
        zSetOperations.add(event.eventId, job2, System.nanoTime().toDouble())
        zSetOperations.add(event.eventId, job3, System.nanoTime().toDouble())


        //when
        val totalSize = zSetOperations.size(event.eventId)
        val rank1 = zSetOperations.rank(event.eventId, job1)
        val rank2 = zSetOperations.rank(event.eventId, job2)
        val rank3 = zSetOperations.rank(event.eventId, job3)

        //then
        assertThat(rank1).isEqualTo(0)
        assertThat(totalSize).isEqualTo(3)
    }


    @Test
    @DisplayName("key 삭제")
    fun deleteKey() {
        //given
        val event = createEventRequest().toEntity()
        val job1 =Job(event.eventId, UUID.randomUUID().toString())
        val job2 =Job(event.eventId, UUID.randomUUID().toString())
        val job3 =Job(event.eventId, UUID.randomUUID().toString())
        zSetOperations.add(event.eventId, job1, System.nanoTime().toDouble())
        zSetOperations.add(event.eventId, job2, System.nanoTime().toDouble())
        zSetOperations.add(event.eventId, job3, System.nanoTime().toDouble())


        //when
        val remove = zSetOperations.remove(event.eventId, job1)
        val totalSize = zSetOperations.size(event.eventId)

        //then
        assertThat(remove).isNotNull
        assertThat(totalSize).isEqualTo(2)
    }

    @Test
    @DisplayName("key의 범위 조회")
    fun getRetrieveFromRange() {
        //given
        val event = createEventRequest().toEntity()
        repeat(100) {
            zSetOperations.add(
                event.eventId,Job(event.eventId, UUID.randomUUID().toString()), System.nanoTime().toDouble()
            )
        }

        //when
        val range = zSetOperations.range(event.eventId, 0, 9)

        //then
        assertThat(range?.size).isEqualTo(10)
    }

    @Test
    @DisplayName("key의 범위 삭제")
    fun deleteFromRangeWithKey() {
        //given
        val event = createEventRequest().toEntity()
        repeat(100) {
            zSetOperations.add(
                event.eventId, Job(event.eventId, UUID.randomUUID().toString()), System.nanoTime().toDouble()
            )
        }

        //when
        val range = zSetOperations.removeRange(event.eventId, 0, 9)
        val size = zSetOperations.size(event.eventId)
        //then
        assertThat(size).isEqualTo(90)
    }
}