package com.flab.inqueue.infra.config

import com.flab.inqueue.infra.property.RedisProperty
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer


@Profile("dev")
@Configuration
class EmbeddedRedisConfig(
    private val redisProperty: RedisProperty,
) {
    private lateinit var redisServer:RedisServer


    @PostConstruct
    fun redisServer() {
        redisServer = RedisServer(redisProperty.port)
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }
}