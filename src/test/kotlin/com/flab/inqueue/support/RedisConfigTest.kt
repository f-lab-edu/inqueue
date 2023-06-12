package com.flab.inqueue.support

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.infra.property.RedisProperty
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisKeyExpiredEvent
import org.springframework.data.redis.core.RedisKeyValueAdapter
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component


@TestConfiguration
@EnableConfigurationProperties(value = [RedisProperty::class])
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
class RedisConfigTest(
    private val redisProperty: RedisProperty,
) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(redisProperty.host, redisProperty.port))
    }

    @Bean(name = ["jobRedisTemplate"])
    fun jobRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Job> {
        val redisTemplate = RedisTemplate<String, Job>()
        redisTemplate.setEnableTransactionSupport(true)
        val objectMapper = jacksonObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(objectMapper, Job::class.java)
        redisTemplate.setConnectionFactory(connectionFactory)
        return redisTemplate
    }

    @Bean(name = ["redisTemplate"])
    fun redisTemplate(): RedisTemplate<*, *> {
        val redisTemplate: RedisTemplate<*, *> = RedisTemplate<Any, Any>()
        redisTemplate.setEnableTransactionSupport(true)
        redisTemplate.setConnectionFactory(redisConnectionFactory()!!)
        return redisTemplate
    }

    @Component
    class SessionExpiredEventListener {
        private val log = LoggerFactory.getLogger(SessionExpiredEventListener::class.java)

        @EventListener
        fun handleRedisKeyExpiredEvent(event: RedisKeyExpiredEvent<String>) {
            log.info("JobUser key={} has expired", String(event.id))
        }
    }
}