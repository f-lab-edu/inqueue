package com.flab.inqueue.dto

import java.time.LocalDateTime

data class QueueResponse(
   val status: QueueStatus,
   val expectedInfo : QueueInfo
)

data class QueueInfo(val time: LocalDateTime,var order : Int)

enum class QueueStatus{
   WAIT,ENTER
}