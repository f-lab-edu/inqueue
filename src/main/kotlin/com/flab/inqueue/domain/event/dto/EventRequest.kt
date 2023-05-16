package com.flab.inqueue.domain.event.dto

import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.entity.WaitQueuePeriod
import org.jetbrains.annotations.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.*


data class EventRequest(

    var eventId: String? = null,
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @field:NotNull val waitQueueStartTime: LocalDateTime,
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @field:NotNull val waitQueueEndTime: LocalDateTime,
    @field:NotNull val jobQueueSize: Long,
    @field:NotNull val jobQueueLimitTime: Long,

    val eventInformation: EventInformation? = null,
    val redirectUrl: String? = null,
) {
    fun toEntity(): Event {
        val eventId = this.eventId?.let { this.eventId } ?: UUID.randomUUID().toString()
        val eventInfo = this.eventInformation?.let { this.eventInformation } ?: EventInformation()

        return Event(
            eventId,
            WaitQueuePeriod(waitQueueStartTime, waitQueueEndTime),
            jobQueueSize,
            jobQueueLimitTime,
            eventInfo,
            redirectUrl
        )
    }
}