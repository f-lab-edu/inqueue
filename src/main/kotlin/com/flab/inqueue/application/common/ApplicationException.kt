package com.flab.inqueue.application.common

import org.springframework.http.HttpStatus

abstract class ApplicationException(
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST, message: String
) : RuntimeException(message)