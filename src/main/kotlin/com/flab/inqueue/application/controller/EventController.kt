package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.event.dto.EventRequest
import com.flab.inqueue.domain.event.dto.EventResponse
import com.flab.inqueue.domain.event.service.EventService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/events")
class EventController(
    private val eventService: EventService,
) {
    @PostMapping
    fun createEvent(
        @RequestHeader("Authorization") accessKey: String,
        @RequestBody @Valid eventRequest: EventRequest,
    ): EventResponse {
        return eventService.save(eventRequest)
    }

}

