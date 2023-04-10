package com.flab.inqueue.security.jwt.utils

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.security.SignatureException

@Component
class JwtUtils(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,

    @Value("\${jwt.expiration-mills}")
    private val expirationMills: Long
) {
    private lateinit var signingKey: Key

    @PostConstruct
    private fun init() {
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    fun create() {
        TODO()
    }

    fun verify(accessToken: String): VerifyJwtResponse {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(signingKey).build().parseClaimsJws(accessToken).body
            return VerifyJwtResponse(userId = claims.subject, isValid = true)

        } catch (ex: Exception) {
            when (ex) {
                is UnsupportedJwtException,
                is MalformedJwtException,
                is SignatureException,
                is ExpiredJwtException -> {
                    return VerifyJwtResponse(isValid = false, throwable = ex.cause)
                }

                else -> throw ex
            }
        }
    }
}