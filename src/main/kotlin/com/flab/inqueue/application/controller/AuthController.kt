package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.auth.dto.TokenRequest
import com.flab.inqueue.domain.auth.dto.TokenResponse
import com.flab.inqueue.domain.auth.service.TokenService
import com.flab.inqueue.security.common.CommonPrincipal
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/server/v1/auth")
class AuthController(
    val tokenService: TokenService
) {
    @PostMapping("/token")
    fun issueToken(@AuthenticationPrincipal principal: CommonPrincipal): TokenResponse {
        return tokenService.generateToken(TokenRequest(principal.clientId))
    }
}
