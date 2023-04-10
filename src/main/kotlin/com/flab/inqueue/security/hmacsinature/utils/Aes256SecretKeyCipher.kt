package com.flab.inqueue.security.hmacsinature.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class Aes256SecretKeyCipher(
     @Value("\${customer.account.client-secret-salt}") salt: String) : SecretKeyCipher(salt) {

    // TODO : 관련 알고리즘 공부 후 구현 필요

    override fun encrypt(stringToEncrypt: String): String {
        // TODO
        return stringToEncrypt
    }

    override fun decrypt(stringToEncrypt: String): String {
        // TODO
       return stringToEncrypt
    }
}