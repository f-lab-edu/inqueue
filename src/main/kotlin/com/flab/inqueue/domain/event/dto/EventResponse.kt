package com.flab.inqueue.domain.event.dto

import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.entity.EventInformation
import java.time.LocalDateTime

data class EventResponse(
    val eventId: String
) {
    var waitQueueStartTime: LocalDateTime? = null
    var waitQueueEndTime: LocalDateTime? = null
    var jobQueueSize: Long? = null
    var jobQueueLimitTime: Long? = null
    var eventInformation: EventInformation? = null
    var redirectUrl : String? = null
    constructor(event: Event) : this(event.eventId){
        this.waitQueueStartTime = event.period.startDateTime
        this.waitQueueEndTime = event.period.endDateTime
        this.jobQueueSize = event.jobQueueSize
        this.jobQueueLimitTime = event.jobQueueLimitTime
        this.eventInformation = event.eventInfo
        this.redirectUrl = event.redirectUrl
    }
}