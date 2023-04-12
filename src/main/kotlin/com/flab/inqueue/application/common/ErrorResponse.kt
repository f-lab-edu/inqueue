package com.flab.inqueue.application.common

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val error: String,
    val status: Int,
    val path: String
) {
    companion object {
        private const val DEFAULT_UNAUTHORIZED_ERROR_MESSAGE = "Unauthorized"
        private const val DEFAULT_FORBIDDEN_ERROR_MESSAGE = "Forbidden"

        @JvmStatic
        fun unAuthorized(request: HttpServletRequest): ErrorResponse {
            return ErrorResponse(
                timestamp = LocalDateTime.now(),
                error = DEFAULT_UNAUTHORIZED_ERROR_MESSAGE,
                status = HttpStatus.UNAUTHORIZED.value(),
                path = request.requestURI
            )
        }

        @JvmStatic
        fun forbidden(request: HttpServletRequest): ErrorResponse {
            return ErrorResponse(
                timestamp = LocalDateTime.now(),
                error = DEFAULT_FORBIDDEN_ERROR_MESSAGE,
                status = HttpStatus.FORBIDDEN.value(),
                path = request.requestURI
            )
        }
    }
}