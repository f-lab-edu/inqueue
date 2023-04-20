package com.flab.inqueue.domain.queue.entity

enum class JobStatus {
    ENTER {
        override val prefix = "ENTER_QUEUE"
    },
    WAIT {
        override val prefix  = "WAIT_QUEUE"
    };
    abstract val prefix : String
}