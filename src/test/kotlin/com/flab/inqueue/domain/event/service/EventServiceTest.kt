package com.flab.inqueue.domain.event.service

import com.flab.inqueue.domain.event.exception.EventNotFoundException
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.member.entity.Member
import com.flab.inqueue.domain.member.entity.MemberKey
import com.flab.inqueue.domain.member.repository.MemberRepository
import com.flab.inqueue.fixture.createEventRequest
import com.flab.inqueue.support.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import java.util.*

@UnitTest
class EventServiceTest {

    @MockK
    lateinit var eventRepository: EventRepository

    @InjectMockKs
    lateinit var eventService: EventService

    lateinit var member: Member

    @MockK
    lateinit var memberRepository: MemberRepository

    @BeforeEach
    fun setUp() {
        val key = MemberKey(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        member = Member(name = "testMember", key = key)
        every { memberRepository.findByKeyClientId(key.clientId) }.returns(member)
    }

    @Test
    @DisplayName("이벤트 조회 성공")
    fun succeedToRetrieve() {
        //given
        val eventId = UUID.randomUUID().toString()
        val member = Member(name = "testMember", key = MemberKey("testClientId", "testClientSecret"))
        val event = createEventRequest().toEntity(eventId, member)

        every { eventRepository.findByEventId(eventId) } returns event

        //when
        val eventResponse = eventService.retrieve(member.key.clientId, eventId)

        //then
        assertAll(
            { assertThat(eventResponse.eventId).isEqualTo(eventId) },
            { assertThat(eventResponse.waitQueueStartTime).isNotNull() },
            { assertThat(eventResponse.waitQueueEndTime).isNotNull() },
            { assertThat(eventResponse.jobQueueSize).isNotNull() },
            { assertThat(eventResponse.jobQueueLimitTime).isNotNull() }
        )
    }

    @Test
    @DisplayName("잘못된 eventId가 주어졌을때 이벤트 검색 실패")
    fun failToRetrieveWhenGivenWrongEventId() {
        //given
        val clientId = "test_client_id"
        val eventId = "test_event_id"
        every { eventRepository.findByEventId(eventId) } returns null

        //when & then
        assertThrows<EventNotFoundException> { eventService.retrieve(clientId, eventId) }
    }

    @Test
    @DisplayName("이벤트 저장 성공")
    fun succeedToSave() {
        //given
        val eventRequest = createEventRequest()
        val eventId = UUID.randomUUID().toString()
        val event = eventRequest.toEntity(eventId, member)
        every { eventRepository.save(event) } returns event

        //when
        val eventResponse = eventService.save(member.key.clientId, eventRequest)

        //then
        assertAll(
            { assertThat(eventResponse.eventId).isEqualTo(eventId) }
        )
    }
}