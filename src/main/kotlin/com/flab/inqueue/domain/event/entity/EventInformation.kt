package com.flab.inqueue.domain.event.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class EventInformation(
    @Column(length = 30)
    val name: String? = null,
    @Column
    val startTime: LocalDateTime? = null,
    @Column
    val endTime: LocalDateTime? = null,
    @Column
    val description: String? = null,
    @Column
    val place: String? = null,
    @Column
    val personnel: Long? = null,
    @Column
    val type: String? = null
)
