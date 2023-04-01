package com.flab.inqueue.domain.event.dto

import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.entity.EventInformation
import java.time.LocalDateTime

data class EventResponse(
    val eventId: String
) {
    private var waitQueueStartTime: LocalDateTime? = null
    private var waitQueueEndTime: LocalDateTime? = null
    private var jobQueueSize: Long? = null
    private var jobQueueLimitTime: Long? = null
    private var eventInformation: EventInformation? = null
    private var redirectUrl : String? = null
    constructor(event: Event) : this(event.eventId){
        this.waitQueueStartTime = event.period.startDateTime
        this.waitQueueEndTime = event.period.endDateTime
        this.jobQueueSize = event.jobQueueSize
        this.jobQueueLimitTime = event.jobQueueLimitTime
        this.eventInformation = event.eventInfo
        this.redirectUrl = event.redirectUrl
    }
}