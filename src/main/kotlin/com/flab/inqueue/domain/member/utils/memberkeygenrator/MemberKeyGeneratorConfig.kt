package com.flab.inqueue.domain.member.utils.memberkeygenrator

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom

@Configuration
class MemberKeyGeneratorConfig(
    val memberKeySizeProperties: MemberKeySizeProperties
) {
    @Bean
    fun memberKeyGenerator(): MemberKeyGenerator {
        return MemberKeyGenerator(securityRandomGenerateStrategy())
    }

    private fun securityRandomGenerateStrategy(): SecureRandomGenerationStrategy {
        return SecureRandomGenerationStrategy(SecureRandom(), memberKeySizeProperties)
    }
}