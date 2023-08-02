package com.flab.inqueue.domain.event.exception

import org.springframework.http.HttpStatus

class EventNotFoundException(message: String) : EventException(HttpStatus.NOT_FOUND, message)