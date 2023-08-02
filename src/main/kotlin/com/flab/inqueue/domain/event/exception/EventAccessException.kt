package com.flab.inqueue.domain.event.exception

import org.springframework.http.HttpStatus

class EventAccessException(httpStatus: HttpStatus = HttpStatus.FORBIDDEN, message: String) :
    EventException(httpStatus, message)