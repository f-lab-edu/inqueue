package com.flab.inqueue

import com.flab.inqueue.domain.event.dto.EventRequest
import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.dto.EventInformation
import java.time.LocalDateTime


fun createEvent(
    eventId: String? = null,
    waitQueueStartDateTime: LocalDateTime = LocalDateTime.now(),
    waitQueueEndDateTime: LocalDateTime = LocalDateTime.now().plusDays(2),
    jobQueueSize: Long = 10,
    jobQueueLimitTime: Long = 10L,
    eventinfo : EventInformation = EventInformation(),
    redirectUrl: String? =  "http://inqueue.test.com"
): Event {
    return createEventRequest(
        eventId,
        waitQueueStartDateTime,
        waitQueueEndDateTime,
        jobQueueSize,
        jobQueueLimitTime,
        eventinfo,
        redirectUrl
    ).toEntity()
}




fun createEventRequest(
    eventId : String?= null,
    waitQueueStartDateTime: LocalDateTime = LocalDateTime.now(),
    waitQueueEndDateTime: LocalDateTime = LocalDateTime.now().plusDays(2),
    jobQueueSize: Long = 10,
    jobQueueLimitTime: Long = 10L,
    eventinfo : EventInformation = EventInformation(),
    redirectUrl: String? = "http://inqueue.test.com"
): EventRequest {
    return EventRequest(eventId,waitQueueStartDateTime,waitQueueEndDateTime,jobQueueSize,jobQueueLimitTime,eventinfo,redirectUrl)
}