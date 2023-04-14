package com.flab.inqueue.security.hmacsinature.utils

interface EncryptionUtil {

    fun encrypt(stringToEncrypt: String): String

    fun decrypt(stringToDecrypt: String): String

}