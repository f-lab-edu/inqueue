package com.flab.inqueue.dto

import java.time.LocalDateTime


data class EventRequest(
    val name: String,
    val description: String,
    val place: String,
    val time: LocalDateTime,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val personnel: Long,
    val waitQueueStartTime: LocalDateTime,
    val waitQueueEndTime: LocalDateTime,
    val type: String,
    val jobQueueSize: Long,
    val jobQueueLimitTime: LocalDateTime,
    val redirectUrl: String
)

data class EventResponse(
    val eventId : String
)