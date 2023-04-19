package com.flab.inqueue.domain.member.utils

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom

@Configuration
class MemberKeyFactoryConfig(
    val properties: MemberKeyProperties
) {

    @Bean
    fun memberKeyFactory(): MemberKeyFactory {
        return MemberKeyFactory(SecureRandom(), properties.clientIdLength, properties.clientSecretLength)
    }
}