package com.flab.inqueue.security.hmacsinature.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.security.SecureRandom
import java.util.*

class AES256EncryptionUtilTest {

    @Test
    @DisplayName("secret key 가 32byte 가 아닌 경우 초기화 실패")
    fun initialize_fail() {
        val secretKey = "abc"
        assertThatThrownBy { AES256EncryptionUtil(secretKey) }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("The secret key for aes256 is not 32 bytes.")
    }

    @Test
    @DisplayName("encrypt 성공")
    fun encrypt_success() {
        val secretKey = UUID.randomUUID().toString().replace("-", "")
        val encryptionUtils = AES256EncryptionUtil(secretKey)

        val oldMessage = getMessage()
        val encryptedMessage = encryptionUtils.encrypt(oldMessage)

        assertThat(encryptedMessage).isBase64()
        assertThat(oldMessage).isNotEqualTo(String(Base64.getDecoder().decode(encryptedMessage)))
    }

    @Test
    @DisplayName("decrypt 성공")
    fun decrypt_success() {
        val secretKey = UUID.randomUUID().toString().replace("-", "")
        val encryptionUtils = AES256EncryptionUtil(secretKey)

        val oldMessage = getMessage()
        val encryptedMessage = encryptionUtils.encrypt(oldMessage)

        assertThat(encryptionUtils.decrypt(encryptedMessage)).isEqualTo(oldMessage)
    }

    private fun getMessage(): String {
        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }
}