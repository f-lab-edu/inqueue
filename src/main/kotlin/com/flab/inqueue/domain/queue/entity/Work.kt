package com.flab.inqueue.domain.queue.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("work")
data class Work(
    @Id
    @Indexed
    val eventId: String? = null,
    val score: Long? = null,
    val time: Long? = null,
)
