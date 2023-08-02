package com.flab.inqueue.domain.event.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class EventInformation(
    @Column(length = 30) val name: String = "",
    val startTime: LocalDateTime = LocalDateTime.now(),
    val endTime: LocalDateTime = LocalDateTime.now(),
    @Column(length = 100) val description: String = "",
    @Column(length = 100) val place: String = "",
    val personnel: Long = 0L,
    val type: String = "",
)
