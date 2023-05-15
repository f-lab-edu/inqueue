package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.event.dto.EventRequest
import com.flab.inqueue.domain.event.dto.EventResponse
import com.flab.inqueue.domain.event.dto.EventRetrieveResponse
import com.flab.inqueue.domain.event.service.EventService
import com.flab.inqueue.security.common.CommonPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("server/v1/events")
class EventController(
    private val eventService: EventService,
) {
    @PostMapping
    fun createEvent(
        @RequestBody @Valid eventRequest: EventRequest,
    ): EventResponse {
        val principal = SecurityContextHolder.getContext().authentication.principal as CommonPrincipal
        return eventService.save(principal.clientId, eventRequest)
    }

    @GetMapping("/{eventId}")
    fun retrieveEvent(
        @PathVariable eventId: String
    ): EventRetrieveResponse {
        val principal = SecurityContextHolder.getContext().authentication.principal as CommonPrincipal
        return eventService.retrieve(principal.clientId, eventId)
    }

    @GetMapping
    fun retrieveEvent(
    ): List<EventRetrieveResponse> {
        val principal = SecurityContextHolder.getContext().authentication.principal as CommonPrincipal
        return eventService.retrieveAll(principal.clientId)
    }

    @PutMapping("/{eventId}")
    fun updateEvent(
        @PathVariable eventId: String,
        @RequestBody @Valid eventRequest: EventRequest,
    ) {
        val principal = SecurityContextHolder.getContext().authentication.principal as CommonPrincipal
        return eventService.update(principal.clientId, eventId, eventRequest)
    }

    @DeleteMapping("/{eventId}")
    fun deleteEvent(
        @PathVariable eventId: String
    ) {
        val principal = SecurityContextHolder.getContext().authentication.principal as CommonPrincipal
        eventService.delete(principal.clientId, eventId)
    }
}

