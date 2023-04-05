package com.flab.inqueue.infra.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("spring.data.redis")
data class RedisProperty(
    val host: String = "",
    val port: Int = 0,
    val password: String = "",
)