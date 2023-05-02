package com.flab.inqueue.infra.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.infra.property.RedisProperty
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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


@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
class RedisConfig(
    private val redisProperty: RedisProperty,
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory? {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
        redisStandaloneConfiguration.hostName = redisProperty.host
        redisStandaloneConfiguration.port = redisProperty.port
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean(name = ["jobRedisTemplate"])
    fun jobRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Job> {
        val redisTemplate = RedisTemplate<String, Job>()
        redisTemplate.setEnableTransactionSupport(true)
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(jacksonObjectMapper(), Job::class.java)
        redisTemplate.setConnectionFactory(connectionFactory)
        return redisTemplate
    }

    @Bean
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
            log.info("redis key={} has expired", String(event.id))
        }
    }
}