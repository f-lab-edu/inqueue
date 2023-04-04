package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.dto.AuthRequest
import com.flab.inqueue.domain.dto.AuthResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController {

    @PostMapping("/token")
    fun generateToken(
        @RequestHeader("Authorization") accessKey: String,
        @RequestBody authRequest: AuthRequest,
    ): AuthResponse {
        return AuthResponse("JWT Token")
    }
}