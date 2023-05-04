package com.flab.inqueue.domain.event.db

import com.flab.inqueue.fixture.createEvent
import com.flab.inqueue.fixture.createEventRequest
import com.flab.inqueue.domain.event.dto.EventInformation
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.support.RepositoryTest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime

@RepositoryTest
class EventRepositoryTest(
    private val eventRepository: EventRepository,
) {

    val firstTestEvent = createEvent()

    @BeforeEach
    fun setUp() {
        eventRepository.saveAll(listOf(firstTestEvent, createEvent()))
    }

    @Test
    @DisplayName("eventId retrieve")
    fun retrieve() {
        //when
        val event = eventRepository.findByEventId(firstTestEvent.eventId)
        //then
        assertAll(
            { assertThat(event).isNotNull },
            { assertThat(event!!.eventId).isNotNull },
            { assertThat(event!!.jobQueueSize).isNotNull },
            { assertThat(event!!.jobQueueLimitTime).isNotNull },
            { assertThat(event!!.period).isNotNull }
        )
    }

    @Test
    @DisplayName("event retrieveAll")
    fun retrieveAll() {
        //given

        // when
        val events = eventRepository.findAll()
        // then
        assertThat(!events.isNullOrEmpty()).isTrue
        assertThat(events.size).isGreaterThanOrEqualTo(2)
    }

    @Test
    @DisplayName("event update")
    fun update() {
        //given
        val findEvent = eventRepository.findByEventId(firstTestEvent.eventId)!!
        val testEventInfo = EventInformation(
            "test",
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now(),
            "test description",
            "test place",
            10L,
            "TEST-CONCERT"
        )
        val testRedirectUrl = "http://testUrl"
        val updateEventRequest = createEventRequest(
            findEvent.eventId, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2),
            500, 12334, testEventInfo, testRedirectUrl
        )
        findEvent.update(updateEventRequest.toEntity())
        // when
        val modifiedEvent = eventRepository.findByEventId(findEvent.eventId)!!
        // then
        assertThat(modifiedEvent).isNotNull
        assertThat(modifiedEvent.eventId).isEqualTo(findEvent.eventId)
        assertThat(modifiedEvent.eventInfo).isEqualTo(testEventInfo)
        assertThat(modifiedEvent.redirectUrl).isEqualTo(testRedirectUrl)
    }

    @Test
    @DisplayName("event delete")
    fun delete() {
        //given
        val findEvent = eventRepository.findByEventId(firstTestEvent.eventId)
        findEvent?.id?.let { eventRepository.deleteById(it) }
        //when
        val reFindEvent = eventRepository.findByEventId(firstTestEvent.eventId)
        // then
        assertThat(reFindEvent).isNull()
    }
}