package com.flab.inqueue.security.hmacsinature

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun createHmacSignature(payLoad: String, clientSecret: String): String {
    val sha256HMAC = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(clientSecret.toByteArray(), "HmacSHA256")
    sha256HMAC.init(secretKey)
    return Base64.encodeBase64String(sha256HMAC.doFinal(payLoad.toByteArray()))
}

fun createHmacAuthorizationHeader(clientId: String, hmacSignature: String): String {
    return "$clientId:$hmacSignature"
}