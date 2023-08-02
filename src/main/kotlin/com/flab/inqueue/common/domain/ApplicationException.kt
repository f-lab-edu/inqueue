package com.flab.inqueue.common.domain

import org.springframework.http.HttpStatus

abstract class ApplicationException(
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST, message: String
) : RuntimeException(message)