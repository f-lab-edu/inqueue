package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.event.service.EventService
import com.flab.inqueue.domain.queue.dto.JobResponse
import com.flab.inqueue.domain.queue.dto.WaitQueueInfo
import com.flab.inqueue.domain.queue.entity.JobStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/events/{eventId}")
class WaitQueueController(
    private val eventService: EventService,
) {

    @PostMapping("/enter")
    fun enterWaitQueue(
        @RequestHeader("Authorization") accessToken: String,
        @PathVariable("eventId") eventId: String,
    ): JobResponse {
        return JobResponse(JobStatus.WAIT, WaitQueueInfo(1L, 1))
    }

    @GetMapping("")
    fun retrieveWaitQueue(
        @RequestHeader("Authorization") accessToken: String,
        @RequestHeader("X-Client-Id") clientId: String,
        @PathVariable eventId: String,
    ): JobResponse {
        return JobResponse(JobStatus.WAIT, WaitQueueInfo(1L, 1))
    }
}

