package com.flab.inqueue.security.controller

import com.flab.inqueue.security.common.CommonPrincipal
import com.flab.inqueue.security.jwt.JwtAuthenticationToken
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SecurityTestController {

    @GetMapping("/server/hmac-security-test")
    fun hmacSecurityTest(@AuthenticationPrincipal principal: CommonPrincipal): CommonPrincipal {
        return principal
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/server/hmac-security-test-with-admin-role")
    fun hmacSecurityTestWithAdminRole(@AuthenticationPrincipal principal: CommonPrincipal): CommonPrincipal {
        return principal
    }

    @GetMapping("/client/jwt-security-test")
    fun jwtSecurityTest(@AuthenticationPrincipal principal: CommonPrincipal): CommonPrincipal {
        return principal
    }
}