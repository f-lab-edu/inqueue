package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.queue.dto.QueueInfo
import com.flab.inqueue.domain.queue.dto.JobResponse
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.JobRedisRepository
import com.flab.inqueue.domain.queue.repository.WaitQueueRedisRepository
import org.springframework.stereotype.Service

@Service
class JobService(
    private val eventRepository: EventRepository,
    private val waitQueueRedisRepository: WaitQueueRedisRepository,
    private val jobRedisRepository: JobRedisRepository
) {

    fun enter(eventId: String, userId: String): JobResponse {
        val event = findEvent(eventId)

        if (isEnterJob(event)) {
            val job = Job(eventId, userId, JobStatus.ENTER, event.jobQueueLimitTime)
            jobRedisRepository.register(job)
            return JobResponse(job.status)
        }

        val job = Job(eventId, userId, JobStatus.WAIT)
        waitQueueRedisRepository.register(job)
        return waitQueueRetrieve(event, job)
    }

    fun retrieve(eventId: String, userId: String): JobResponse {
        val job = Job(eventId, userId, JobStatus.ENTER)
        if (jobRedisRepository.isMember(job)) {
            return JobResponse(job.status)
        }

        val event = findEvent(eventId)
        val waitJob = Job(eventId, userId, JobStatus.WAIT)
        return waitQueueRetrieve(event, waitJob)
    }

    private fun findEvent(eventId: String): Event {
        return eventRepository.findByEventId(eventId) ?: throw NoSuchElementException("행사를 찾을 수 없습니다. $eventId")
    }

    private fun isEnterJob(event: Event): Boolean {
        val jobQueueSize = jobRedisRepository.size(JobStatus.ENTER.makeRedisKey(event.eventId)) ?: 0
        val waitQueueSize = waitQueueRedisRepository.size(JobStatus.WAIT.makeRedisKey(event.eventId)) ?: 0

        return waitQueueSize == 0L && jobQueueSize < event.jobQueueLimitTime
    }

    fun waitQueueRetrieve(event: Event, job: Job): JobResponse {
        if (!waitQueueRedisRepository.isMember(job)) return JobResponse(JobStatus.TIMEOUT)
        val rank = (waitQueueRedisRepository.rank(job) ?: 0) + 1
        val waitTimePerPerson = event.jobQueueLimitTime / event.jobQueueSize
        val waitSecond = rank * waitTimePerPerson
        return JobResponse(JobStatus.WAIT, QueueInfo(waitSecond, rank.toInt()))
    }
}