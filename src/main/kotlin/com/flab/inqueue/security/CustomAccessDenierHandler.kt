package com.flab.inqueue.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.flab.inqueue.application.common.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDenierHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {

    companion object {
        private const val DEFAULT_FORBIDDEN_ERROR_MESSAGE = "Forbidden"
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val jsonResponse = ErrorResponse(
            error = DEFAULT_FORBIDDEN_ERROR_MESSAGE,
            status = HttpServletResponse.SC_FORBIDDEN,
            path = request.requestURI
        )
        response.writer.write(objectMapper.writeValueAsString(jsonResponse))
    }
}