package com.flab.inqueue.domain.event.dto

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
data class EventInformation(
    @Column(length = 30)
    val name: String? = null,
    val startTime: LocalDateTime? = null,
    @Column
    val endTime: LocalDateTime? = null,
    @Column
    val description: String? = null,
    val place: String? = null,
    val personnel: Long? = null,
    val type: String? = null
)
