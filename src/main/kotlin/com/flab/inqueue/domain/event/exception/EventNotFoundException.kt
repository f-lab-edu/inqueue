package com.flab.inqueue.domain.event.exception

class EventNotFoundException(message: String) : EventException(404, message) {
}