package com.flab.inqueue.fixture

import com.flab.inqueue.domain.event.entity.EventInformation
import com.flab.inqueue.domain.event.dto.EventRequest
import java.time.LocalDateTime

fun createEventRequest(
    startDateTime: LocalDateTime = LocalDateTime.now(),
    endDateTime: LocalDateTime = LocalDateTime.now().plusDays(2),
    jobQueueSize: Long = 10,
    jobQueueLimitTime: Long = 10L,
    eventInfo: EventInformation = EventInformation(),
    redirectUrl: String? = "http://inqueue.test.com"
): EventRequest {
    return EventRequest(
        startDateTime, endDateTime, jobQueueSize, jobQueueLimitTime, eventInfo, redirectUrl
    )
}