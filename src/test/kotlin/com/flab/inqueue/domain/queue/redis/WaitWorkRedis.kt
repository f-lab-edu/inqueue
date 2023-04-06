package com.flab.inqueue.domain.queue.redis

import com.flab.inqueue.createEventRequest
import com.flab.inqueue.domain.queue.repository.QueueRedisRepository
import com.flab.inqueue.support.EmbeddedRedisConfig
import org.junit.jupiter.api.AfterEach
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
//    private val queueRedisRepository: QueueRedisRepository,
    private val redisTemplate: RedisTemplate<String,Any>
) {
    private val logger = LoggerFactory.getLogger( WaitWorkRedis::class.java )

//    @AfterEach
//    fun tearDown() {
//        queueRedisRepository.deleteAll()
//    }

    @Test
    fun test() {

    }
    @Test
    fun `등록`() {
        //given

        val event = createEventRequest().toEntity()
        val toList = (0..10).map { i ->
            redisTemplate.opsForZSet().add(event.eventId, i , System.nanoTime().toDouble() )
            event
        }

        redisTemplate.opsForZSet().add(event.eventId, 1 , System.nanoTime().toDouble())

//        val toHashSet= toList.map { it.score }.toHashSet()


        //when
        val range = redisTemplate.opsForZSet().range(event.eventId, 0, -1)

        range
//        log.info("대기열에 추가 - {} ({}초)", people, now)
//        queueRedisRepository.saveAll(toList)

        //then
//        val findAll = queueRedisRepository.findAll()
//        val groupBy = findAll.groupBy { it.score }
//        val filter = groupBy.values.filter { it.size >= 2 }

//        assertThat(findAll.count()).isEqualTo(toHashSet.size)
//        findAll.forEach {
//            logger.info("check : data {}", it.toString())
//            println("check ${it.toString()}")
//            assertAll(
//                { assertThat(it.eventId).isNotNull() },
//            )
//        }
    }
}