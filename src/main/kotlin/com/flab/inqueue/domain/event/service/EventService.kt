package com.flab.inqueue.domain.event.service

import com.flab.inqueue.domain.event.dto.EventRequest
import com.flab.inqueue.domain.event.dto.EventResponse
import com.flab.inqueue.domain.event.dto.EventRetrieveResponse
import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.exception.EventAccessException
import com.flab.inqueue.domain.event.exception.EventNotFoundException
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.member.exception.MemberNotFoundException
import com.flab.inqueue.domain.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class EventService(
    private val eventRepository: EventRepository,
    private val memberRepository: MemberRepository
) {
    fun retrieve(clientId: String, eventId: String): EventRetrieveResponse {
        val foundEvent = findEvent(eventId)
        if (!foundEvent.isAccessible(clientId)) {
            throw EventAccessException("해당 이벤트에 접근할 수 없습니다. eventId=${eventId}")
        }

        return toEventRetrieveResponse(foundEvent)
    }

    fun retrieveAll(customId: String): List<EventRetrieveResponse> {
        return eventRepository.findAllByMemberKeyClientId(customId).map { toEventRetrieveResponse(it) }
    }

    @Transactional
    fun save(clientId: String, request: EventRequest): EventResponse {
        val member = memberRepository.findByKeyClientId(clientId) ?: throw MemberNotFoundException(clientId)
        val eventId = UUID.randomUUID().toString()
        val savedEvent = eventRepository.save(request.toEntity(eventId, member))
        return EventResponse(savedEvent.eventId)
    }

    @Transactional
    fun update(clientId: String, eventId: String, request: EventRequest) {
        var foundEvent = findEvent(eventId)
        if (!foundEvent.isAccessible(clientId)) {
            throw EventAccessException("해당 이벤트에 접근할 수 없습니다. eventId=$eventId")
        }
        foundEvent.update(request.toEntity(eventId, foundEvent.member))
    }

    @Transactional
    fun delete(clientId: String, eventId: String) {
        var foundEvent = findEvent(eventId)
        if (!foundEvent.isAccessible(clientId)) {
            throw EventAccessException("해당 이벤트에 접근할 수 없습니다. eventId=$eventId")
        }
        eventRepository.deleteById(foundEvent.id)
    }

    private fun findEvent(eventId: String): Event {
        return eventRepository.findByEventId(eventId)
            ?: throw EventNotFoundException("행사를 찾을 수 없습니다. eventId=${eventId}")
    }

    private fun toEventRetrieveResponse(event: Event): EventRetrieveResponse {
        return EventRetrieveResponse(
            event.eventId,
            event.period.startDateTime,
            event.period.endDateTime,
            event.jobQueueSize,
            event.jobQueueLimitTime,
            event.eventInfo,
            event.redirectUrl
        )
    }
}