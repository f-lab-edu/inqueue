package com.flab.inqueue.security.jwt.utils

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import io.jsonwebtoken.security.SignatureException
import java.security.Key
import java.time.ZoneId
import java.util.*

@Component
class JwtUtils(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,

    @Value("\${jwt.expiration-mills}")
    private val expirationMills: Long
) {
    companion object {
        private const val CLAIM_USER_ID_CODE = "userId"
        private const val CLAIM_CLIENT_ID_CODE = "clientId"
        private const val TOKEN_ISSUER = "com.inqueue"
    }
    private var signingKey: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun create(clientId: String, userId: String): JwtToken {
        val now = System.currentTimeMillis()
        val expiryDate = Date(now + expirationMills)

        val claimsMap = mutableMapOf<String, String>()
        claimsMap[CLAIM_CLIENT_ID_CODE] = clientId
        claimsMap[CLAIM_USER_ID_CODE] = userId

        val accessToken: String = Jwts.builder()
            .setIssuer(TOKEN_ISSUER)
            .setClaims(claimsMap)
            .setIssuedAt(Date(now))
            .setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()

        return JwtToken(accessToken, expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
    }

    fun verify(accessToken: String): JwtVerificationResponse {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(signingKey).build().parseClaimsJws(accessToken).body

            return JwtVerificationResponse(
                clientId = claims[CLAIM_CLIENT_ID_CODE] as String,
                userId = claims[CLAIM_USER_ID_CODE] as String,
                isValid = true
            )

        } catch (ex: Exception) {
            when (ex) {
                is UnsupportedJwtException,
                is MalformedJwtException,
                is SignatureException,
                is ExpiredJwtException -> {
                    return JwtVerificationResponse(isValid = false, throwable = ex.cause)
                }

                else -> throw ex
            }
        }
    }
}