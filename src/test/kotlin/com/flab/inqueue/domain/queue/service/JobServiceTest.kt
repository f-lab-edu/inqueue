package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.exception.EventAccessException
import com.flab.inqueue.domain.event.exception.EventNotFoundException
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.entity.MemberKey
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.JobRedisRepository
import com.flab.inqueue.fixture.createEventRequest
import com.flab.inqueue.support.UnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@UnitTest
class JobServiceTest {

    private val jobRedisRepository: JobRedisRepository = mockk<JobRedisRepository>(relaxed = true)
    private val eventRepository: EventRepository = mockk<EventRepository>()
    private val waitQueueService: WaitQueueService = mockk<WaitQueueService>(relaxed = true)
    private val jobService: JobService = JobService(jobRedisRepository, eventRepository, waitQueueService)

    lateinit var userId: String
    lateinit var eventId: String
    lateinit var clientId: String
    lateinit var member: Member
    lateinit var event: Event

    @BeforeEach
    fun setUp() {
        userId = "testUserId"
        eventId = "testEventId"
        clientId = "testClientId"
        member = Member(name = "testMember", key = MemberKey(clientId, "testClientSecret"))

        val startDateTime = LocalDateTime.of(2023, 8, 3, 10, 0, 0)
        val endDateTime = LocalDateTime.of(2023, 8, 3, 12, 0, 0)
        event = createEventRequest(startDateTime = startDateTime, endDateTime = endDateTime).toEntity(eventId, member)
        every { eventRepository.findByEventId(eventId) } returns event
    }

    @Test
    @DisplayName("잘못된 eventId를 전달하면 EventNotFoundException 발생한다.")
    fun notFoundEvent() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)
        val invalidEventId = "invalidEventId"
        every { eventRepository.findByEventId(invalidEventId) } returns null

        // when && then
        assertThatThrownBy { jobService.enter(invalidEventId, userId, currentDateTime) }
            .isInstanceOf(EventNotFoundException::class.java)
    }

    @Test
    @DisplayName("행사 시간이 아닌 시간에 접근 할 수 없다.")
    fun notOpenEvent() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 9, 59, 59)
        val invalidEventId = eventId

        // when && then
        val throwable = AssertionsForClassTypes.catchThrowableOfType(
            { jobService.enter(invalidEventId, userId, currentDateTime) },
            EventAccessException::class.java
        )

        assertThat(throwable.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    @DisplayName("JobQueue에 여유가 있고, WaitQueue가 비어있으면 JobQueue 에 들어간다.")
    fun job_enter_job_queue() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)
        every { waitQueueService.size(JobStatus.WAIT.makeRedisKey(eventId)) } returns 0
        every { jobRedisRepository.size(JobStatus.ENTER.makeRedisKey(eventId)) } returns 5

        // when
        val jobResponse = jobService.enter(eventId, userId, currentDateTime)

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
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)
        every { waitQueueService.size(JobStatus.WAIT.makeRedisKey(eventId)) } returns 5
        every { jobRedisRepository.size(JobStatus.ENTER.makeRedisKey(eventId)) } returns 0

        // when
        jobService.enter(eventId, userId, currentDateTime)

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
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)
        val enterJob = Job(eventId, userId, JobStatus.ENTER)
        every { jobRedisRepository.isMember(enterJob) } returns true

        // when
        val jobResponse = jobService.retrieve(eventId, userId, currentDateTime)

        // then
        assertThat(jobResponse.status).isEqualTo(JobStatus.ENTER)
    }

    @Test
    @DisplayName("wait_job 검색")
    fun retrieve_wait_job() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)
        every { jobRedisRepository.isMember(any()) } returns false

        // when
        jobService.retrieve(eventId, userId, currentDateTime)

        // then
        val waitJob = Job(
            eventId = eventId,
            userId = userId,
            jobQueueSize = event.jobQueueSize,
            queueLimitTime = event.jobQueueLimitTime
        )
        verify { waitQueueService.retrieve(waitJob) }
    }

    @Test
    @DisplayName("작업열 검증 성공")
    fun verify_job_queue() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)
        val job = Job(eventId, userId, JobStatus.ENTER)
        every { jobRedisRepository.isMember(job) } returns true

        // when
        val verificationResponse = jobService.verify(eventId, clientId, userId, currentDateTime)

        // then
        assertThat(verificationResponse.isVerified).isTrue()
    }

    @Test
    @DisplayName("작업열 검증 실패")
    fun fail_to_verify_job_queue() {
        // when & then
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)
        val anotherClientId = "otherClientId"
        assertThrows<EventAccessException> { jobService.verify(eventId, anotherClientId, userId, currentDateTime) }
    }

    @Test
    @DisplayName("작업열 종료 성공")
    fun close_job_queue() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)
        val job = Job(eventId, userId, JobStatus.ENTER)
        every { jobRedisRepository.isMember(job) } returns true

        // when
        jobService.close(eventId, clientId, userId, currentDateTime)

        // then
        verify { jobRedisRepository.remove(job) }
    }

    @Test
    @DisplayName("작업열 종료 실패")
    fun fail_to_close_job_queue() {
        // given
        val currentDateTime = LocalDateTime.of(2023, 8, 3, 11, 0, 0)

        // when & then
        val anotherClientId = "otherClientId"
        assertThrows<EventAccessException> { jobService.close(eventId, anotherClientId, userId, currentDateTime) }
    }
}