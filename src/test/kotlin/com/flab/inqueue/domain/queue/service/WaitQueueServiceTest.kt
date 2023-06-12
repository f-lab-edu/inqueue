package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.entity.MemberKey
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.WaitQueueRedisRepository
import com.flab.inqueue.fixture.createEventRequest
import com.flab.inqueue.support.UnitTest
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

@UnitTest
class WaitQueueServiceTest {

    private val waitQueueRedisRepository: WaitQueueRedisRepository = mockk<WaitQueueRedisRepository>()

    private val waitQueueService: WaitQueueService = WaitQueueService(waitQueueRedisRepository)

    @Test
    @DisplayName("대기열 조회 성공")
    fun retrieve_wait_job() {
        //given
        val eventId = "testEventId"
        val userId = "testUserId"
        val member = Member(name = "testMember", key = MemberKey("testClientId", "testClientSecret"))
        val event = createEventRequest().toEntity(eventId, member)

        val job = Job(
            userId = userId,
            eventId = eventId,
            queueLimitTime = event.jobQueueLimitTime,
            jobQueueSize = event.jobQueueSize
        )
        val expectedRank = 5L
        every { waitQueueRedisRepository.rank(job) } returns expectedRank
        every { waitQueueRedisRepository.isMember(job) } returns true
        every { waitQueueRedisRepository.updateUserTtl(job) } returns Unit

        //given
        val jobResponse = waitQueueService.retrieve(job)

        //then
        assertThat(jobResponse.expectedInfo!!.order).isEqualTo(job.waitTimePerOneJob * (expectedRank + 1))
    }


    @Test
    @DisplayName("대기열 조회 Time Out")
    fun retrieve_time_out_job() {
        // given
        val userId = "testUserId"
        val eventId = "testEventId"

        val job = Job(
            userId = userId,
            eventId = eventId,
        )
        every { waitQueueService.isMember(job) } returns false

        // when
        val jobResponse = waitQueueService.retrieve(job)

        // then
        assertThat(jobResponse.status).isEqualTo(JobStatus.TIMEOUT)
    }
}