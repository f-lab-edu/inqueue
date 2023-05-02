package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.createEvent
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.JobRedisRepository
import com.flab.inqueue.support.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.jupiter.api.DisplayName

@UnitTest
class JobServiceTest {

    private val jobRedisRepository: JobRedisRepository = mockk<JobRedisRepository>(relaxed = true)

    private val eventRepository: EventRepository = mockk<EventRepository>()

    private val waitQueueService: WaitQueueService = mockk<WaitQueueService>(relaxed = true)

    private val jobService: JobService = JobService(jobRedisRepository, eventRepository, waitQueueService)

    @Test
    @DisplayName("JobQueue에 여유가 있고, WaitQueue가 비어있으면 JobQueue 에 들어간다.")
    fun job_enter_job_queue() {
        // given
        val userId = "testUserId"
        val eventId = "testEventId"
        val event = createEvent(eventId = eventId)

        every { eventRepository.findByEventId(eventId) } returns event
        every { waitQueueService.size(JobStatus.WAIT.makeRedisKey(eventId)) } returns 0
        every { jobRedisRepository.size(JobStatus.ENTER.makeRedisKey(eventId)) } returns 5

        // when
        val jobResponse = jobService.enter(eventId, userId)

        // then
        val enterJob = Job(eventId, userId, JobStatus.ENTER, event.jobQueueLimitTime)
        verify { jobRedisRepository.register(enterJob) }

        val waitJob = Job(eventId, userId, JobStatus.WAIT, event.jobQueueLimitTime, event.jobQueueSize)
        verify(exactly = 0) { waitQueueService.register(waitJob) }

        assertThat(jobResponse.status).isEqualTo(JobStatus.ENTER)
    }

    @Test
    @DisplayName("JobQueue에 여유가 있으나, WaitQueue가 비어있지 않으면 WaitQueue 에 들어간다.")
    fun job_enter_wait_queue() {
        // given
        val userId = "testUserId"
        val eventId = "testEventId"
        val event = createEvent(eventId = eventId)

        every { eventRepository.findByEventId(eventId) } returns event
        every { waitQueueService.size(JobStatus.WAIT.makeRedisKey(eventId)) } returns 5
        every { jobRedisRepository.size(JobStatus.ENTER.makeRedisKey(eventId)) } returns 0

        // when
        jobService.enter(eventId, userId)

        // then
        val waitJob = Job(eventId, userId, JobStatus.WAIT, event.jobQueueLimitTime, event.jobQueueSize)
        verify { waitQueueService.register(waitJob) }

        val enterJob = Job(eventId, userId, JobStatus.ENTER, event.jobQueueLimitTime)
        verify(exactly = 0) { jobRedisRepository.register(enterJob) }
    }

    @Test
    @DisplayName("enter_job 검색")
    fun retrieve_enter_job() {
        // given
        val userId = "testUserId"
        val eventId = "testEventId"
        val enterJob = Job(eventId, userId, JobStatus.ENTER)

        every { jobRedisRepository.isMember(enterJob) } returns true

        // when
        val jobResponse = jobService.retrieve(eventId, userId)

        // then
        assertThat(jobResponse.status).isEqualTo(JobStatus.ENTER)
    }

    @Test
    @DisplayName("wait_job 검색")
    fun retrieve_wait_job() {
        // given
        val userId = "testUserId"
        val eventId = "testEventId"
        val event = createEvent(eventId)

        every { eventRepository.findByEventId(any()) } returns event
        every { jobRedisRepository.isMember(any()) } returns false
        every { waitQueueService.isMember(any()) } returns true

        // when
        jobService.retrieve(eventId, userId)

        // then
        val waitJob = Job(
            eventId = eventId,
            userId = userId,
            jobQueueSize = event.jobQueueSize,
            jobQueueLimitTime = event.jobQueueLimitTime
        )
        verify { waitQueueService.retrieve(waitJob) }
    }

    @Test
    @DisplayName("time_out_job 검색")
    fun retrieve_time_out_job() {
        // given
        val userId = "testUserId"
        val eventId = "testEventId"

        every { eventRepository.findByEventId(any()) } returns createEvent(eventId)
        every { jobRedisRepository.isMember(any()) } returns false
        every { waitQueueService.isMember(any()) } returns false

        // when
        val jobResponse = jobService.retrieve(eventId, userId)

        // then
        assertThat(jobResponse.status).isEqualTo(JobStatus.TIMEOUT)
    }
}