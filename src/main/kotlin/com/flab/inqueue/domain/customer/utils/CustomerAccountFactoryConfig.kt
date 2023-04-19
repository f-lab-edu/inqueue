package com.flab.inqueue.domain.customer.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom

@Configuration
class CustomerAccountFactoryConfig {

    @Value("\${customer.account.client-id-length}")
    private val clientIdLength: Int? = null

    @Value("\${customer.account.client-secret-length}")
    private val clientSecretLength : Int? = null

    @Bean
    fun customerAccountFactory() : CustomerAccountFactory {
        return CustomerAccountFactory(SecureRandom(), clientIdLength!!, clientSecretLength!!)
    }
}