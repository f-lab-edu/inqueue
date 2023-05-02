package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.createEvent
import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.repository.WaitQueueRedisRepository
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
        val event = createEvent(eventId)

        val job = Job(
            userId = userId,
            eventId = eventId,
            queueLimitTime = event.jobQueueLimitTime,
            jobQueueSize = event.jobQueueSize
        )
        val expectedRank = 5L
        every { waitQueueRedisRepository.rank(job) } returns expectedRank

        //given
        val jobResponse = waitQueueService.retrieve(job)

        //then
        assertThat(jobResponse.expectedInfo!!.order).isEqualTo(job.waitTimePerOneJob * (expectedRank + 1))
    }
}