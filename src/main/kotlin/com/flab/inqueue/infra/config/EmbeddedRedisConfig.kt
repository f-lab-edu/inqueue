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
    redisProperty: RedisProperty
) {
    private val redisServer: RedisServer

    init {
        redisServer = RedisServer(redisProperty.port)
    }

    @PostConstruct
    fun startRedis() {
        redisServer.start()
    }

    @PreDestroy
    fun stopRedis() {
        redisServer.stop()
    }
}