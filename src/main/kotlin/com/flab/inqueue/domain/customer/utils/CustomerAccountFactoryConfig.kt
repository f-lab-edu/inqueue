package com.flab.inqueue.domain.customer.utils

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom

@Configuration
class CustomerAccountFactoryConfig(
    val properties: CustomerAccountProperties
) {

    @Bean
    fun customerAccountFactory(): CustomerAccountFactory {
        return CustomerAccountFactory(SecureRandom(), properties.clientIdLength, properties.clientSecretLength)
    }
}