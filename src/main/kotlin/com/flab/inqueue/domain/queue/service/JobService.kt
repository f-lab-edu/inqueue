package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.queue.dto.QueueResponse
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import org.springframework.stereotype.Service

@Service
class JobService(
    private val eventRepository: EventRepository,
    private val queueService: QueueService,
) {

    fun enter(eventId: String, userId: String): QueueResponse {
        val job = if (isEnterJob(eventId)) Job(eventId, userId, JobStatus.ENTER)
        else Job(eventId, userId, JobStatus.WAIT)
        return queueService.register(job)
    }

    fun retrieve(eventId: String, userId: String): QueueResponse {
        val waitJob = Job(eventId, userId, JobStatus.WAIT)
        return queueService.retrieve(waitJob)
    }

    private fun isEnterJob(eventId: String): Boolean {
        val event = eventRepository.findByEventId(eventId) ?: throw NoSuchElementException("행사를 찾을 수 없습니다. $eventId")

        val enterQueueSize = queueService.size(JobStatus.ENTER.makeRedisKey(eventId)) ?: 0
        val waitQueueSize = queueService.size(JobStatus.WAIT.makeRedisKey(eventId)) ?: 0

        if (waitQueueSize > 0) return false
        if (enterQueueSize > event.jobQueueLimitTime) return false

        return true
    }
}