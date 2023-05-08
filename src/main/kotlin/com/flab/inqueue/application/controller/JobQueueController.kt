package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.queue.dto.JobVerificationResponse
import com.flab.inqueue.domain.queue.service.JobService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/server/v1/events/{eventId}")
class JobQueueController(
    private val jobService: JobService
) {

    @PostMapping("/job-queue-check/{userId}")
    fun validateJobQueue(
        @PathVariable eventId: String,
        @PathVariable userId: String,
    ): JobVerificationResponse {
        return jobService.verify(eventId, userId)
    }

    @PostMapping("/job-queue-finish/{userId}")
    fun closeJopQueue(
        @PathVariable eventId: String,
        @PathVariable userId: String,
    ): ResponseEntity<Unit> {
        jobService.close(eventId, userId)
        return ResponseEntity.ok().build()
    }
}

