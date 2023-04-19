package com.flab.inqueue.domain.member.utils.memberkeygenrator

import com.flab.inqueue.domain.member.entity.MemberKey
import java.security.SecureRandom
import java.util.*

class SecureRandomGenerationStrategy(
    private val secureRandom: SecureRandom,
    private val properties: MemberKeySizeProperties
) : MemberKeyGenerateStrategy {

    override fun generate(): MemberKey {
        val clientIdBytes = ByteArray(properties.clientIdSize)
        secureRandom.nextBytes(clientIdBytes)
        val clientId = Base64.getEncoder().encodeToString(clientIdBytes)

        val clientSecretBytes = ByteArray(properties.clientSecretSize)
        secureRandom.nextBytes(clientSecretBytes)
        val clientSecret = Base64.getEncoder().encodeToString(clientSecretBytes)

        return MemberKey(clientId, clientSecret)
    }
}