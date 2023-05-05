package com.flab.inqueue

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class TestContainer {

    companion object {
        @JvmStatic
        val redisContainer: GenericContainer<*> = GenericContainer("redis:5.0.3-alpine").withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun registerRedisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host", redisContainer::getHost)
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }

        init {
            redisContainer.start()
        }

    }
}