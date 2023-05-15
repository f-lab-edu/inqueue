package com.flab.inqueue.domain.queue.scheduler

import com.flab.inqueue.domain.event.repository.EventRepository
import com.flab.inqueue.domain.queue.service.JobService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EnterJobScheduler(
    private val eventRepository: EventRepository,
    private val jobService: JobService,
    @Qualifier("threadPoolTaskExecutor")
    private val taskExecutor: TaskExecutor
) {
    @Scheduled(cron = "1 * * * * *")
    fun execute() {
        val events = eventRepository.findOngoingEvents(LocalDateTime.now())

        for (event in events) {
            taskExecutor.execute(object : Runnable {
                override fun run() {
                    if (jobService.getWaitQueueSize(event) == 0L) {
                        return
                    }

                    val availableJobQueueSize = jobService.getAvailableJobQueueSize(event)
                    if (availableJobQueueSize <= 0) {
                        return
                    }

                    jobService.enterAll(event, availableJobQueueSize)
                }
            })
        }
    }
}