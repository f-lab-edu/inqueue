package com.flab.inqueue.domain.event.dto

import com.flab.inqueue.domain.event.entity.EventInformation
import java.time.LocalDateTime

data class EventRetrieveResponse(
    val eventId: String,
    val waitQueueStartTime: LocalDateTime? = null,
    val waitQueueEndTime: LocalDateTime? = null,
    val jobQueueSize: Long? = null,
    val jobQueueLimitTime: Long? = null,
    val eventInformation: EventInformation? = null,
    val redirectUrl: String? = null,
)
