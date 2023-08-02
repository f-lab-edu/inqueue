package com.flab.inqueue.domain.event.dto

import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.entity.EventInformation
import com.flab.inqueue.domain.event.entity.WaitQueuePeriod
import com.flab.inqueue.domain.member.entity.Member
import org.jetbrains.annotations.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime


data class EventRequest(
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @field:NotNull var waitQueueStartTime: LocalDateTime,
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @field:NotNull val waitQueueEndTime: LocalDateTime,
    @field:NotNull val jobQueueSize: Long,
    @field:NotNull val jobQueueLimitTime: Long,

    val eventInformation: EventInformation? = null,
    val redirectUrl: String? = null,
) {
    fun toEntity(eventId: String, member: Member): Event {
        val eventInfo =
            this.eventInformation ?: EventInformation(startTime = waitQueueStartTime, endTime = waitQueueEndTime)
        return Event(
            eventId,
            WaitQueuePeriod(waitQueueStartTime, waitQueueEndTime),
            jobQueueSize,
            jobQueueLimitTime,
            eventInfo,
            redirectUrl,
            member
        )
    }
}