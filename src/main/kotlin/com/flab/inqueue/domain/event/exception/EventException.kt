package com.flab.inqueue.domain.event.exception

abstract class EventException(val statusCode: Int = 400, message: String) : RuntimeException(message)