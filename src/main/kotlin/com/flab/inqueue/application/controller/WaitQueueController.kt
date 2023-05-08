package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.queue.dto.JobResponse
import com.flab.inqueue.domain.queue.service.JobService
import com.flab.inqueue.security.common.CommonPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/client/v1/events/{eventId}")
class WaitQueueController(
    private val jobService: JobService
) {

    @PostMapping("/enter")
    fun enterWaitQueue(
        @PathVariable("eventId") eventId: String,
    ): JobResponse {
        val principal = SecurityContextHolder.getContext().authentication as CommonPrincipal
        return jobService.enter(eventId, principal.userId!!)
    }

    @GetMapping
    fun retrieveWaitQueue(
        @PathVariable eventId: String,
    ): JobResponse {
        val principal = SecurityContextHolder.getContext().authentication as CommonPrincipal
        return jobService.retrieve(eventId, principal.userId!!)
    }
}

