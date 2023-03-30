package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.dto.EventResponse
import com.flab.inqueue.domain.dto.QueueInfo
import com.flab.inqueue.domain.dto.QueueResponse
import com.sun.jdi.request.EventRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalTime

@RestController
@RequestMapping("/v1/events")
class EventController {

    @PostMapping
    fun createEvent(
        @RequestHeader("Authorization") accessKey: String,
        eventRequest: EventRequest
    ): EventResponse {
        return EventResponse("eventId")
    }

    @PostMapping("/{eventId}/enter")
    fun enterWaitQueue(
        @RequestHeader("Authorization") accessToken: String,
        @PathVariable("eventId") eventId: String,
    ): QueueResponse {
        return QueueResponse("WAIT", QueueInfo(LocalTime.now(), 1))
    }


    @GetMapping("/{eventId}")
    fun retrieveWaitQueue(
        @RequestHeader("Authorization") accessToken: String,
        @RequestHeader("X-Client-Id") clientId: String,
        @PathVariable eventId: String,
    ): QueueResponse {
        return QueueResponse("WAIT", QueueInfo(LocalTime.now(), 1))
    }


    @PostMapping("/{eventId}/job-queue-check/{userId}")
    fun validateJobQueue(
        @RequestHeader("Authorization") accessKey: String,
        @PathVariable eventId: String,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }


    @PostMapping("/{eventId}/job-queue-finish/{userId}")
    fun closeJopQueue(
        @RequestHeader("Authorization") accessKey: String,
        @PathVariable eventId: String,
        @PathVariable userId: String
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }
}