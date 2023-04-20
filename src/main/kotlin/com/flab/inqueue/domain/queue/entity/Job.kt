package com.flab.inqueue.domain.queue.entity

class Job(
    val eventId: String,
    val userId: String,
) {

    companion object{
        fun from(job : Job): Job {
            return Job(job.eventId,job.userId)
        }
        fun enterJobFrom(job : Job): Job {
            val stringBuffer = StringBuilder()
            stringBuffer.append(job.eventId)
            stringBuffer.append(":JOB_QUEUE")
            return Job(stringBuffer.toString(),job.userId)
        }
    }

}
