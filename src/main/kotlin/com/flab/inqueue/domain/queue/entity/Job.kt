package com.flab.inqueue.domain.queue.entity

class Job(
    var eventId: String,
    val userId: String,
    var status : JobStatus = JobStatus.WAIT
) {
    init {
        val stringBuffer = StringBuilder(status.prefix)
        stringBuffer.append(eventId)
        this.eventId = stringBuffer.toString()
    }
}
