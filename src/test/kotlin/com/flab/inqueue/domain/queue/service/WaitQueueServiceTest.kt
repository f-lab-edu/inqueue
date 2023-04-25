package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.queue.entity.Job
import com.flab.inqueue.domain.queue.entity.JobStatus
import com.flab.inqueue.domain.queue.repository.WaitQueueRedisRepository
import com.flab.inqueue.support.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.*

@UnitTest
class WaitQueueServiceTest {

    @MockK
    lateinit var waitQueueRedisRepository: WaitQueueRedisRepository

    @InjectMockKs
    lateinit var waitQueueService: WaitQueueService


    @DisplayName("대기열 및 작업열 진입")
    @ParameterizedTest(name = "{index} ==> ''{0}'' test")
    @EnumSource(JobStatus::class)
    fun enterJobQueue(jobStatus: JobStatus) {
        //given
        val eventId = "TestEventId"
        val userId = UUID.randomUUID().toString()

        //given
        every { waitQueueRedisRepository.register(any()) } returns Unit
        every { waitQueueRedisRepository.rank(any()) } returns 1

        val queueResponse = waitQueueService.waitQueueRegister(Job(eventId, userId, jobStatus))

        //then
        assertThat(queueResponse.status).isEqualTo(jobStatus)

        if (queueResponse.status == JobStatus.ENTER) {
            assertThat(queueResponse.expectedInfo).isNull()
        } else {
            assertThat(queueResponse.expectedInfo?.second).isGreaterThan(0)
            assertThat(queueResponse.expectedInfo?.order).isGreaterThan(0)
        }
    }

    @Test
    @DisplayName("대기열 조회")
    fun enterWaitQueue() {
        //given
        val eventId = "TestEventId"
        val userId = UUID.randomUUID().toString()

        every { waitQueueRedisRepository.rank(any()) } returns 1

        //given
        val queueResponse = waitQueueService.waitQueueRetrieve(Job(eventId, userId))

        //then
        assertThat(queueResponse.status).isEqualTo(JobStatus.WAIT)
        assertThat(queueResponse.expectedInfo?.second).isGreaterThan(0)
        assertThat(queueResponse.expectedInfo?.order).isGreaterThan(0)
    }
}