package com.flab.inqueue.domain.event.exception

class EventAccessException(message: String) : EventException(403, message)