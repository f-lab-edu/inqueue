package com.flab.inqueue.domain.event.entity

import com.flab.inqueue.common.domain.BaseEntity
import com.flab.inqueue.domain.event.dto.EventInformation
import com.flab.inqueue.domain.member.entity.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Table(
    uniqueConstraints = [UniqueConstraint(name = "uk_event", columnNames = ["eventId"])],
    indexes = [Index(name = "idx_event_id", columnList = "eventId")]
)
@Entity
class Event(
    @Column(nullable = false) val eventId: String,
    @Embedded var period: WaitQueuePeriod,
    @Column(nullable = false) var jobQueueSize: Long,
    @Column(nullable = false) var jobQueueLimitTime: Long,
    @Embedded var eventInfo: EventInformation? = null,
    var redirectUrl: String?,
    @ManyToOne(fetch = FetchType.LAZY)
    val member: Member,
    @Column(nullable = false, updatable = false) val createdDateTime: LocalDateTime = LocalDateTime.now(),
) : BaseEntity() {
    @Column(nullable = false)
    var modifiedDateTime: LocalDateTime = LocalDateTime.now()
        private set

    fun update(event: Event) {
        this.period = event.period
        this.jobQueueSize = event.jobQueueSize
        this.jobQueueLimitTime = event.jobQueueLimitTime
        this.eventInfo = event.eventInfo
        this.redirectUrl = event.redirectUrl
        this.modifiedDateTime = LocalDateTime.now()
    }

    fun isAccessible(clientId : String) :Boolean {
        return this.member.key.clientId == clientId
    }
}
