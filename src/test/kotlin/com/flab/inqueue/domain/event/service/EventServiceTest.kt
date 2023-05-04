package com.flab.inqueue.domain.event.service

import com.flab.inqueue.fixture.createEvent
import com.flab.inqueue.fixture.createEventRequest
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.support.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*

@UnitTest
class EventServiceTest {

    @MockK
    lateinit var eventRepository: EventRepository

    @InjectMockKs
    lateinit var eventService: EventService


    @Test
    @DisplayName("이벤트 조회 성공")
    fun successRetrieve() {
        //given
        val createdEvent = createEvent()
        val requestEvent = createEventRequest(createdEvent.eventId)
        every { eventRepository.findByEventId(any()) } returns createdEvent

        //when
        val eventResponse = eventService.retrive(requestEvent)

        //then
        assertAll(
            { assertThat(eventResponse.eventId).isEqualTo(createdEvent.eventId) },
            { assertThat(eventResponse.waitQueueStartTime).isNotNull() },
            { assertThat(eventResponse.waitQueueEndTime).isNotNull() },
            { assertThat(eventResponse.jobQueueSize).isNotNull() },
            { assertThat(eventResponse.jobQueueLimitTime).isNotNull() }
        )
    }

    @Test
    @DisplayName("이벤트 조회 실패 잘못된 eventId")
    fun failRetrieve() {
        //given
        val createdEvent = createEvent()
        val requestEvent = createEventRequest(eventId = "testUUID")
        every { eventRepository.findByEventId(any()) } returns null

        //when
        val exception = assertThrows<NoSuchElementException> { eventService.retrive(requestEvent) }

        //Then
        assertThat(exception.message).isEqualTo("행사를 찾을 수 없습니다. ${requestEvent.eventId}")
    }

    @Test
    @DisplayName("이벤트 조회 상황에서 eventId가 null 혹은 빈값이 올때")
    fun isNullOrBlankEventId() {
        //given
        val createdEvent = createEvent()
        val requestEvent = createEventRequest()
        every { eventRepository.findByEventId(any()) } returns createdEvent

        //when
        val exception = assertThrows<IllegalArgumentException> { eventService.retrive(requestEvent) }

        //Then
        assertThat(exception.message).isEqualTo("eventId를 입력해주세요")
    }

    @Test
    @DisplayName("이벤트 저장 성공")
    fun successSave() {
        //given
        val createdEvent = createEvent()
        val requestEvent = createEventRequest()
        every { eventRepository.save(any()) } returns createdEvent

        //when
        val eventResponse = eventService.save(requestEvent)

        //then
        assertAll(
            { assertThat(eventResponse.eventId).isEqualTo(createdEvent.eventId) }
        )
    }
}