package com.flab.inqueue.domain.event.service

import com.flab.inqueue.domain.event.dto.EventRequest
import com.flab.inqueue.domain.event.dto.EventResponse
import com.flab.inqueue.domain.event.dto.EventRetrieveResponse
import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.repository.EventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class EventService(
    private val eventRepository: EventRepository,
) {
    fun retrive(request: EventRequest): EventRetrieveResponse {
        val findEvent = findEvent(request)
        return EventRetrieveResponse(
            findEvent.eventId,
            findEvent.period.startDateTime,
            findEvent.period.endDateTime,
            findEvent.jobQueueSize,
            findEvent.jobQueueLimitTime,
            findEvent.eventInfo,
            findEvent.redirectUrl
        )
    }

    fun retriveAll(customId: String) {
        // TODO: 고객사 도메인 미구현 // return eventRepository.findAllByCustomId(customId)
    }

    @Transactional
    fun save(request: EventRequest) = EventResponse(eventRepository.save(request.toEntity()).eventId)

    @Transactional
    fun update(request: EventRequest) {
        var findEvent = findEvent(request)
        findEvent.update(request.toEntity())
    }

    @Transactional
    fun delete(request: EventRequest) = eventRepository.deleteById(findEvent(request).id)

    private fun validateRequest(request: EventRequest) = require(!request.eventId.isNullOrBlank()) { "eventId를 입력해주세요" }
    private fun findEvent(request: EventRequest): Event {
        validateRequest(request)
        return eventRepository.findByEventId(request.eventId!!)
            ?: throw NoSuchElementException("행사를 찾을 수 없습니다. ${request.eventId}")
    }

}