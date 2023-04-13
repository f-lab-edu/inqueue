package com.flab.inqueue.domain.queue.service

import com.flab.inqueue.domain.dto.QueueInfo
import com.flab.inqueue.domain.dto.QueueResponse
import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.queue.entity.Job
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class JobService(
    private val eventRepository: EventRepository,
    private val queueService: QueueService,
) {

    fun enter(eventId : String,userId : String): QueueResponse {
        val waitJob = Job(eventId, userId)
        if(isEnterJob(eventId)){
            val enterJob = Job.enterJobFrom(waitJob)
            queueService.register(enterJob)

            return QueueResponse("ENTIER",null)
        }
        //대기열 진입
        queueService.register(waitJob)
        return queueResponse(waitJob)
    }

    fun retrieve(eventId : String,userId : String): QueueResponse {
        val waitJob = Job(eventId, userId)
        return queueResponse(waitJob)
    }

    private fun isEnterJob(eventId : String): Boolean {
        val event = eventRepository.findByEventId(eventId) ?: throw NoSuchElementException("행사를 찾을 수 없습니다. $eventId")
        val size = queueService.size(event.eventId) ?: 0

        if( event.jobQueueLimitTime > size ) {
            return true
        }
        return false;
    }

    private fun queueResponse(job : Job): QueueResponse {
        //대기열 진입
        val rank = (queueService.rank(job) ?: 0) + 1
        val waitMin = rank * 3 * 60
        val waitTime = LocalTime.now().plusMinutes(waitMin)
        return QueueResponse("WAIT",QueueInfo(waitTime, rank.toInt()))
    }


}