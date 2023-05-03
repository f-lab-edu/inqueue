package com.flab.inqueue.domain.member.entity

import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import jakarta.persistence.Embeddable

@Embeddable
class MemberKey(
    var clientId: String,
    var clientSecret: String,
) {
    fun encrypt(encryptionUtil: EncryptionUtil): MemberKey {
        return MemberKey(clientId, encryptionUtil.encrypt(this.clientSecret))
    }
}