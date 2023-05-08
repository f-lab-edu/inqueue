package com.flab.inqueue

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class TestContainer {

    companion object {
        @JvmStatic
        val mySQLContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0.23").withDatabaseName("test-db")
        @JvmStatic
        val redisContainer: GenericContainer<*> = GenericContainer("redis:7.0.11").withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun registerRedisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host", redisContainer::getHost)
            registry.add("spring.data.redis.port") { redisContainer.getMappedPort(6379).toString() }
        }

        init {
            mySQLContainer.start()
            redisContainer.start()
        }

    }
}