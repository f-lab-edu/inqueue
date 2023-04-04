package com.flab.inqueue.domain.event.dto

import com.flab.inqueue.domain.event.entity.Event
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.*


data class EventRequest(

    var eventId:String?= null,
    @field:NotNull
    var waitQueueStartTime: LocalDateTime,
    @field:NotNull
    val waitQueueEndTime: LocalDateTime,
    @field:NotNull
    val jobQueueSize: Long,
    @field:NotNull
    val jobQueueLimitTime: Long,

    val eventInformation: EventInformation? = null,
    val redirectUrl : String? = null
) {
    fun toEntity(): Event {
        val eventId = this.eventId?.let{ this.eventId } ?: UUID.randomUUID().toString().take(8)
        val eventInfo = this.eventInformation?.let { this.eventInformation } ?: EventInformation()

        return Event(eventId, waitQueueStartTime, waitQueueEndTime, jobQueueSize, jobQueueLimitTime,eventInfo, redirectUrl)
    }
}