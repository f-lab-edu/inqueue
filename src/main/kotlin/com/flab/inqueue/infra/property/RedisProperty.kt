package com.flab.inqueue.infra.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.data.redis")
data class RedisProperty(
    val host: String,
    val port: Int,
)