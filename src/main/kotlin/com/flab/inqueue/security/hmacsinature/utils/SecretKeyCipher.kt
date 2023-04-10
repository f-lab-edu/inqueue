package com.flab.inqueue.security.hmacsinature.utils

abstract class SecretKeyCipher(protected val salt: String) {

    abstract fun encrypt(stringToEncrypt: String): String

    abstract fun decrypt(stringToEncrypt: String): String
}