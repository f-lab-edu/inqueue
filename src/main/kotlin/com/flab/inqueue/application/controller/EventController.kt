package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.event.dto.EventRequest
import com.flab.inqueue.domain.event.dto.EventResponse
import com.flab.inqueue.domain.event.dto.EventRetrieveResponse
import com.flab.inqueue.domain.event.service.EventService
import com.flab.inqueue.security.common.CommonPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/server/v1/events")
class EventController(
    private val eventService: EventService,
) {
    @PostMapping
    fun createEvent(
        @RequestBody @Valid eventRequest: EventRequest,
        @AuthenticationPrincipal principal: CommonPrincipal,
    ): EventResponse {
        return eventService.save(principal.clientId, eventRequest)
    }

    @GetMapping("/{eventId}")
    fun retrieveEvent(
        @PathVariable eventId: String,
        @AuthenticationPrincipal principal: CommonPrincipal
    ): EventRetrieveResponse {
        return eventService.retrieve(principal.clientId, eventId)
    }

    @GetMapping
    fun retrieveEvent(
        @AuthenticationPrincipal principal: CommonPrincipal,
    ): List<EventRetrieveResponse> {
        return eventService.retrieveAll(principal.clientId)
    }

    @PutMapping("/{eventId}")
    fun updateEvent(
        @PathVariable eventId: String,
        @RequestBody @Valid eventRequest: EventRequest,
        @AuthenticationPrincipal principal: CommonPrincipal,
    ) {
        return eventService.update(principal.clientId, eventId, eventRequest)
    }

    @DeleteMapping("/{eventId}")
    fun deleteEvent(
        @PathVariable eventId: String,
        @AuthenticationPrincipal principal: CommonPrincipal,
    ) {
        eventService.delete(principal.clientId, eventId)
    }
}

