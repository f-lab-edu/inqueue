package com.flab.inqueue.security.hmacsinature.utils

interface EncryptionUtil {

    fun encrypt(messageToEncrypt: String): String

    fun decrypt(messageToDecrypt: String): String

}