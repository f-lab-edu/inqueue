package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.queue.dto.JobVerificationResponse
import com.flab.inqueue.domain.queue.service.JobService
import com.flab.inqueue.security.common.CommonPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/server/v1/events/{eventId}")
class JobQueueController(
    private val jobService: JobService
) {

    @PostMapping("/job-queue-check/{userId}")
    fun validateJobQueue(
        @PathVariable eventId: String,
        @PathVariable userId: String,
        @AuthenticationPrincipal principal: CommonPrincipal,
    ): JobVerificationResponse {
        return jobService.verify(eventId, principal.clientId, userId, LocalDateTime.now())
    }

    @PostMapping("/job-queue-finish/{userId}")
    fun closeJopQueue(
        @PathVariable eventId: String,
        @PathVariable userId: String,
        @AuthenticationPrincipal principal: CommonPrincipal,
    ): ResponseEntity<Unit> {
        jobService.close(eventId, principal.clientId, userId, LocalDateTime.now())
        return ResponseEntity.ok().build()
    }
}

