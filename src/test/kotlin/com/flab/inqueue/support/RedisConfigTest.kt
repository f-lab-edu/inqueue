package com.flab.inqueue.support

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.flab.inqueue.domain.queue.entity.Work
import com.flab.inqueue.infra.property.RedisProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@TestConfiguration
@EnableConfigurationProperties(value = [RedisProperty::class])
class RedisConfigTest(
    private val redisProperty: RedisProperty,
) {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(redisProperty.host, redisProperty.port))
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(jacksonObjectMapper(), Work::class.java)
        redisTemplate.setConnectionFactory(connectionFactory)
        return redisTemplate
    }
}