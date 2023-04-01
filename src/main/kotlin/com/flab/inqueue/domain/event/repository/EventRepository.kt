package com.flab.inqueue.domain.event.repository

import com.flab.inqueue.domain.event.entity.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<Event,Long> {

    fun findByEventId(eventId : String) : Event?

}