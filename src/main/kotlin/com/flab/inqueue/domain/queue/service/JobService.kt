package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.queue.dto.JobResponse
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.JobRedisRepository
import org.springframework.stereotype.Service

@Service
class JobService(
    private val jobRedisRepository: JobRedisRepository,
    private val eventRepository: EventRepository,
    private val waitQueueService: WaitQueueService,
) {

    fun enter(eventId: String, userId: String): JobResponse {
        val event = findEvent(eventId)

        if (isEnterJob(event)) {
            val job = Job(eventId, userId, JobStatus.ENTER, event.jobQueueLimitTime)
            jobRedisRepository.register(job)
            return JobResponse(job.status)
        }

        val waitJob = Job(
            eventId = eventId,
            userId = userId,
            jobQueueSize = event.jobQueueSize,
            jobQueueLimitTime = event.jobQueueLimitTime
        )
        return waitQueueService.register(waitJob)
    }

    fun retrieve(eventId: String, userId: String): JobResponse {
        val job = Job(eventId, userId, JobStatus.ENTER)
        if (jobRedisRepository.isMember(job)) {
            return JobResponse(job.status)
        }

        val event = findEvent(eventId)
        val waitJob = Job(
            eventId = eventId,
            userId = userId,
            jobQueueSize = event.jobQueueSize,
            jobQueueLimitTime = event.jobQueueLimitTime
        )
        if (!waitQueueService.isMember(job)) {
            return JobResponse(JobStatus.TIMEOUT)
        }
        return waitQueueService.retrieve(waitJob)
    }

    private fun findEvent(eventId: String): Event {
        return eventRepository.findByEventId(eventId) ?: throw NoSuchElementException("행사를 찾을 수 없습니다. $eventId")
    }

    private fun isEnterJob(event: Event): Boolean {
        val waitQueueSize = waitQueueService.size(JobStatus.WAIT.makeRedisKey(event.eventId))
        val jobQueueSize = jobRedisRepository.size(JobStatus.ENTER.makeRedisKey(event.eventId))

        return waitQueueSize == 0L && jobQueueSize < event.jobQueueLimitTime
    }
}