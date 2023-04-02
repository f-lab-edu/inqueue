package com.flab.inqueue.domain.event.service

import com.flab.inqueue.domain.event.dto.EventRequest
import com.flab.inqueue.domain.event.dto.EventResponse
import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.event.repository.EventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
class EventService(
    private val eventRepository: EventRepository
) {
    fun retrive(request: EventRequest) = EventResponse(findEvent(request))
    fun retriveAll(customId : String) {
        // TODO: 고객사 도메인 미구현 // return eventRepository.findAllByCustomId(customId)
    }
    fun save(request: EventRequest) = EventResponse(eventRepository.save(request.toEntity()).eventId)

    @Transactional
    fun update(request: EventRequest) {
        val findEvent = findEvent(request)
        findEvent.update(request.waitQueueStartTime, request.waitQueueEndTime
            ,request.jobQueueLimitTime, request.jobQueueSize, request.eventInformation!!, request.redirectUrl)
    }

    fun delete(request: EventRequest) = eventRepository.deleteById(findEvent(request).id)

    private fun validateRequest(request: EventRequest) = require( !request.eventId.isNullOrBlank() ) { "eventId를 입력해주세요" }
    private fun findEvent(request: EventRequest): Event {
        validateRequest(request)
        return eventRepository.findByEventId(request.eventId!!)
            ?: throw NoSuchElementException("행사를 찾을 수 없습니다. ${request.eventId}")
    }

}