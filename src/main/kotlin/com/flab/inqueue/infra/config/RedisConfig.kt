package com.flab.inqueue.infra.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.flab.inqueue.domain.queue.entity.Work
import com.flab.inqueue.infra.property.RedisProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
@EnableRedisRepositories
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

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(jacksonObjectMapper(), Work::class.java)
        redisTemplate.setConnectionFactory(connectionFactory)
        return redisTemplate
    }
}