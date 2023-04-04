package com.flab.inqueue.domain.event.entity

import com.flab.inqueue.common.domain.BaseEntity
import com.flab.inqueue.domain.event.dto.EventInformation
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(
    uniqueConstraints = [
        UniqueConstraint(name = "uk_event", columnNames = ["eventId"])
    ],
    indexes = [Index(name = "idx_event_id", columnList = "eventId")]
)
@Entity
class Event(
    @Column(nullable = false)
    val eventId: String, // 필수이며, 임뮤터블 이라고 생각

    period : WaitQueuePeriod, // 필수이지만 , 뮤터블 하다고 생각해서 var를 선언, 대신 @Column으로 nullable 처리 토록 작성
    jobQueueSize: Long,
    jobQueueLimitTime: Long,

    eventInfo : EventInformation = EventInformation(), // 필수가 아니여서 초기화 진행
    redirectUrl: String? = null,

    @Column(nullable = false, updatable = false)
    val createdDateTime: LocalDateTime = LocalDateTime.now(),
    modifiedDateTime: LocalDateTime = LocalDateTime.now(),

    id:Long
) : BaseEntity(id){

    @Embedded
    var period : WaitQueuePeriod = period
        private set
    @Column(nullable = false)
    var jobQueueSize: Long = jobQueueSize
        private set
    @Column(nullable = false)
    var jobQueueLimitTime: Long = jobQueueLimitTime
        private set
    @Embedded
    var eventInfo : EventInformation = eventInfo
        private set
    var redirectUrl: String? = redirectUrl
        private set
    @Column(nullable = false)
    var modifiedDateTime: LocalDateTime = modifiedDateTime
        private set

    constructor(
        eventId: String,
        waitQueueStartDateTime: LocalDateTime,
        waitQueueEndDateTime: LocalDateTime,
        jobQueueSize: Long,
        jobQueueLimitTime: Long,
        eventinfo : EventInformation,
        redirectUrl: String?
    ) : this(eventId,WaitQueuePeriod(waitQueueStartDateTime, waitQueueEndDateTime),jobQueueSize,jobQueueLimitTime, id = 0L) {
        this.eventInfo = eventinfo
        this.redirectUrl = redirectUrl
    }

    fun update(
        waitQueueStartDateTime: LocalDateTime,
        waitQueueEndDateTime: LocalDateTime,
        jobQueueSize: Long,
        jobQueueLimitTime: Long,
        eventInfo : EventInformation,
        redirectUrl: String?
    ) {
        this.period = WaitQueuePeriod(waitQueueStartDateTime,waitQueueEndDateTime)
        this.jobQueueSize = jobQueueSize
        this.jobQueueLimitTime = jobQueueLimitTime
        this.eventInfo = eventInfo
        this.redirectUrl = redirectUrl
        this.modifiedDateTime = LocalDateTime.now()
    }
}
