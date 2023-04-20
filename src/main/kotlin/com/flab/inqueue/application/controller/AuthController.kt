package com.flab.inqueue.application.controller

import com.flab.inqueue.domain.auth.dto.TokenRequest
import com.flab.inqueue.domain.auth.service.TokenService
import com.flab.inqueue.security.common.CommonPrincipal
import com.flab.inqueue.security.jwt.utils.JwtToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/server/v1/auth")
class AuthController(
    val tokenService: TokenService
) {

    @PostMapping("/token")
    fun issueToken(
        @RequestBody request: TokenRequest
    ): JwtToken? {
        val principal = SecurityContextHolder.getContext().authentication.principal as CommonPrincipal
        return tokenService.generateToken(TokenRequest(principal.clientId, request.userId))
    }
}
