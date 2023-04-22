package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.event.entity.Event
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
        val event = findEvent(eventId)

        val job =
            if (isEnterJob(event)) Job(eventId, userId, JobStatus.ENTER, event.period.convertInstant())
            else Job(eventId, userId, JobStatus.WAIT, event.period.convertInstant())

        return queueService.waitQueueRegister(job)
    }


    fun retrieve(eventId: String, userId: String): QueueResponse {
        val waitJob = Job(eventId, userId, JobStatus.WAIT)
        return queueService.waitQueueRetrieve(waitJob)
    }

    private fun findEvent(eventId: String): Event {
        return eventRepository.findByEventId(eventId) ?: throw NoSuchElementException("행사를 찾을 수 없습니다. $eventId")
    }

    private fun isEnterJob(event: Event): Boolean {

        // 에서 조회를 해야함
        val enterQueueSize = queueService.size(JobStatus.ENTER.makeRedisKey(event.eventId)) ?: 0
        val waitQueueSize = queueService.size(JobStatus.WAIT.makeRedisKey(event.eventId)) ?: 0

        if (waitQueueSize > 0) return false
        if (enterQueueSize > event.jobQueueLimitTime) return false

        return true
    }
}