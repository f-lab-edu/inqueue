package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.event.service.EventService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/server/v1/events/{eventId}/")
class JobQueueController(
    private val eventService: EventService,
) {


    @PostMapping("/job-queue-check/{userId}")
    fun validateJobQueue(
        @RequestHeader("Authorization") accessKey: String,
        @PathVariable eventId: String,
        @PathVariable userId: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }


    @PostMapping("/job-queue-finish/{userId}")
    fun closeJopQueue(
        @RequestHeader("Authorization") accessKey: String,
        @PathVariable eventId: String,
        @PathVariable userId: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }
}

