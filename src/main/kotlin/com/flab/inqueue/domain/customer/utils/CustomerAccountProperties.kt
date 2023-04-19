package com.flab.inqueue.domain.customer.utils

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "customer.account")
data class CustomerAccountProperties(
    val clientIdLength: Int,
    var clientSecretLength: Int
)
