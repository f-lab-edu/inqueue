package com.flab.inqueue.domain.event.repository

import com.flab.inqueue.domain.event.dto.EventInformation
import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.entity.MemberKey
import com.flab.inqueue.domain.member.repository.MemberRepository
import com.flab.inqueue.fixture.createEventRequest
import com.flab.inqueue.support.RepositoryTest
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDateTime
import java.util.*

@RepositoryTest
class EventRepositoryTest(
    private val eventRepository: EventRepository,
    private val memberRepository: MemberRepository
) {

    lateinit var testMember: Member

    lateinit var testEventId1: String
    lateinit var testEventId2: String
    lateinit var testEvent1: Event
    lateinit var testEvent2: Event

    @BeforeEach
    fun setUp() {
        testMember = Member(name = "testMember", key = MemberKey("testClientId", "testClientSecret"))
        memberRepository.save(testMember)

        testEventId1 = UUID.randomUUID().toString()
        testEventId2 = UUID.randomUUID().toString()

        testEvent1 = createEventRequest().toEntity(testEventId1, testMember)

        testEventId2 = UUID.randomUUID().toString()
        testEvent2 = createEventRequest().toEntity(testEventId2, testMember)

        eventRepository.saveAll(listOf(testEvent1, testEvent2))
    }

    @Test
    @DisplayName("eventId retrieve")
    fun retrieve() {
        //when
        val event = eventRepository.findByEventId(testEventId1)
        //then
        assertAll(
            { assertThat(event).isNotNull },
            { assertThat(event!!.eventId).isEqualTo(testEventId1) },
            { assertThat(event!!.jobQueueSize).isNotNull },
            { assertThat(event!!.jobQueueLimitTime).isNotNull },
            { assertThat(event!!.period).isNotNull }
        )
    }

    @Test
    @DisplayName("event retrieveAll")
    fun retrieveAll() {
        // when
        val events = eventRepository.findAllByMemberKeyClientId(testMember.key.clientId)
        // then
        assertThat(!events.isNullOrEmpty()).isTrue
        assertThat(events.size).isGreaterThanOrEqualTo(2)
    }

    @Test
    @DisplayName("event update")
    fun update() {
        //given
        val findEvent = eventRepository.findByEventId(testEventId1)!!
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
            LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2),
            500, 12334, testEventInfo, testRedirectUrl
        )
        findEvent.update(updateEventRequest.toEntity(findEvent.eventId, testMember))
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
        val findEvent = eventRepository.findByEventId(testEventId1)
        findEvent?.id?.let { eventRepository.deleteById(it) }
        //when
        val reFindEvent = eventRepository.findByEventId(testEventId1)
        // then
        assertThat(reFindEvent).isNull()
    }
}