package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.queue.dto.JobResponse
import com.flab.inqueue.domain.queue.service.JobService
import com.flab.inqueue.security.common.CommonPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/client/v1/events")
class WaitQueueController(
    private val jobService: JobService
) {

    @PostMapping("/{eventId}/enter")
    fun enterWaitQueue(
        @PathVariable("eventId") eventId: String,
        @AuthenticationPrincipal principal: CommonPrincipal,
    ): JobResponse {
        return jobService.enter(eventId, principal.userId!!, LocalDateTime.now())
    }

    @PostMapping("/{eventId}/exit")
    fun exitWaitQueue(
        @PathVariable("eventId") eventId: String,
        @AuthenticationPrincipal principal: CommonPrincipal,
    ) {
        jobService.exitWaitQueue(eventId, principal.userId!!, LocalDateTime.now())
    }

    @GetMapping("/{eventId}")
    fun retrieveWaitQueue(
        @PathVariable eventId: String,
        @AuthenticationPrincipal principal: CommonPrincipal,
    ): JobResponse {
        return jobService.retrieve(eventId, principal.userId!!, LocalDateTime.now())
    }
}

