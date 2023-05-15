package com.flab.inqueue.domain.event.repository

import com.flab.inqueue.domain.event.entity.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface EventRepository : JpaRepository<Event, Long> {

    fun findByEventId(eventId: String): Event?

    fun findAllByMemberKeyClientId(clientId: String): List<Event>

    @Query("select event from Event event where event.period.startDateTime <= :baseTime and event.period.endDateTime >= :baseTime")
    fun findOngoingEvents(baseTime: LocalDateTime): List<Event>
}