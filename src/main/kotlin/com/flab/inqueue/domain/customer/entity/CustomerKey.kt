package com.flab.inqueue.domain.customer.entity

import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import jakarta.persistence.Embeddable

@Embeddable
class CustomerKey(
    var clientId: String,
    var clientSecret: String,
) {
    fun encryptClientSecret(encryptionUtil: EncryptionUtil) {
        this.clientSecret = encryptionUtil.encrypt(this.clientSecret)
    }
}
