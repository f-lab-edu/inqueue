package com.flab.inqueue.dto

import java.time.LocalTime

data class QueueResponse(
   val status: QueueStatus,
   val expectedInfo : QueueInfo
)

data class QueueInfo(val time: LocalTime,var order : Int)

enum class QueueStatus{
   WAIT,ENTER
}