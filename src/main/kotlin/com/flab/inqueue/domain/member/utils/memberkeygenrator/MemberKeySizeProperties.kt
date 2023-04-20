package com.flab.inqueue.domain.member.utils.memberkeygenrator

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "member.key")
data class MemberKeySizeProperties(
    val clientIdSize: Int,
    var clientSecretSize: Int
)
