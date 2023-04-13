package com.flab.inqueue.security.controller

import com.flab.inqueue.security.hmacsinature.HmacAuthenticationToken
import com.flab.inqueue.security.jwt.JwtAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SecurityTestController {

    @GetMapping("/server/hmac-security-test")
    fun hmacSecurityTest(): HmacAuthenticationToken {
        return SecurityContextHolder.getContext().authentication as HmacAuthenticationToken
    }

    @GetMapping("/client/jwt-security-test")
    fun jwtSecurityTest(): JwtAuthenticationToken {
        return SecurityContextHolder.getContext().authentication as JwtAuthenticationToken
    }
}