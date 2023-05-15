package com.flab.inqueue.domain.queue.scheduler

import com.flab.inqueue.domain.event.entity.Event
import com.flab.inqueue.domain.queue.service.JobService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EnterJobScheduleTask(
    private val jobService: JobService,
) {

    @Async("threadPoolTaskExecutor")
    fun enterJobs(event: Event) {
        if (jobService.getWaitQueueSize(event) == 0L) {
            return
        }
        val availableJobQueueSize = jobService.getAvailableJobQueueSize(event)
        if (availableJobQueueSize <= 0) {
            return
        }
        jobService.enterAll(event, availableJobQueueSize)
    }
}