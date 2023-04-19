package com.flab.inqueue.domain.member.utils

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "member.key")
data class MemberKeyProperties(
    val clientIdLength: Int,
    var clientSecretLength: Int
)
