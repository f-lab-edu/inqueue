package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.event.dto.EventRequest
import com.flab.inqueue.domain.event.dto.EventResponse
import com.flab.inqueue.domain.event.service.EventService
import com.flab.inqueue.domain.queue.dto.QueueInfo
import com.flab.inqueue.domain.queue.dto.QueueResponse
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.service.JobService
import com.flab.inqueue.security.common.CommonPrincipal
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/client/v1/events")
class EventController(
    private val eventService: EventService,
    private val jobService: JobService,
) {
    @PostMapping
    fun createEvent(
        @RequestHeader("Authorization") accessKey: String,
        @RequestBody @Valid eventRequest: EventRequest,
    ): EventResponse {
        return eventService.save(eventRequest)
    }

    @PostMapping("/{eventId}/enter")
    fun enterWaitQueue(
        @PathVariable("eventId") eventId: String,
    ): QueueResponse {
        val principal = SecurityContextHolder.getContext().authentication.principal as CommonPrincipal
        val userId = principal.userId ?: throw Exception("에러")
        val enter = jobService.enter(eventId, userId)
        return enter
    }


    @GetMapping("/{eventId}")
    fun retrieveWaitQueue(
        @RequestHeader("Authorization") accessToken: String,
        @RequestHeader("X-Client-Id") clientId: String,
        @PathVariable eventId: String,
    ): QueueResponse {
        return QueueResponse(JobStatus.WAIT, QueueInfo(1L, 1))
    }


    @PostMapping("/{eventId}/job-queue-check/{userId}")
    fun validateJobQueue(
        @RequestHeader("Authorization") accessKey: String,
        @PathVariable eventId: String,
        @PathVariable userId: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }


    @PostMapping("/{eventId}/job-queue-finish/{userId}")
    fun closeJopQueue(
        @RequestHeader("Authorization") accessKey: String,
        @PathVariable eventId: String,
        @PathVariable userId: String,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok().build()
    }
}

