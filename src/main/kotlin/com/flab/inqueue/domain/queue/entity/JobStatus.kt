package com.flab.inqueue.domain.queue.entity


enum class JobStatus {
    ENTER {
        override val prefix = "JOB_QUEUE"
        override fun makeRedisKey(eventId: String): String {
            return StringBuilder(prefix).append(":").append(eventId).toString()
        }
    },
    WAIT {
        override val prefix = "WAIT_QUEUE"
        override fun makeRedisKey(eventId: String): String {
            return StringBuilder(prefix).append(":").append(eventId).toString()
        }
    },
    TIMEOUT {
        override val prefix: String = ""
        override fun makeRedisKey(eventId: String): String { return ""}
    };

    abstract val prefix: String
    abstract fun makeRedisKey(eventId: String): String
}