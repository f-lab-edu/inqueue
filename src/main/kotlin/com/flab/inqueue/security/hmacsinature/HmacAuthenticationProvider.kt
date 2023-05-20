package com.flab.inqueue.security.hmacsinature

import com.flab.inqueue.domain.member.repository.MemberRepository
import com.flab.inqueue.security.common.CommonPrincipal
import com.flab.inqueue.security.hmacsinature.utils.EncryptionUtil
import com.flab.inqueue.security.hmacsinature.utils.HmacSignatureVerifier
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class HmacAuthenticationProvider(
    private val hmacSignatureVerifier: HmacSignatureVerifier,
    private val memberRepository: MemberRepository,
    private val encryptionUtil: EncryptionUtil
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication {
        val hmacAuthentication = authentication as HmacAuthenticationToken
        val member = memberRepository.findByKeyClientId(authentication.clientId!!)
            ?: throw UsernameNotFoundException("Customer(clientId=${authentication.clientId}) not found")

        val memberKey = member.key

        val isValid = hmacSignatureVerifier.verify(
            signature = hmacAuthentication.signature!!,
            clientSecret = encryptionUtil.decrypt(memberKey.clientSecret),
            payload = hmacAuthentication.payload!!
        )

        if (!isValid) {
            throw BadCredentialsException("Invalid hmac authentication - clientId: ${hmacAuthentication.clientId}")
        }

        val principal = CommonPrincipal(clientId = memberKey.clientId, roles = member.roles)

        return HmacAuthenticationToken.authenticated(
            principal = principal,
            authorities = member.roles.map { SimpleGrantedAuthority("ROLE_$it") }.toMutableList()
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return HmacAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}