package com.flab.inqueue.security

import com.flab.inqueue.security.hmacsinature.HmacAuthenticationProvider
import com.flab.inqueue.security.hmacsinature.HmacSignatureAuthenticationFilter
import com.flab.inqueue.security.jwt.JwtAuthenticationFilter
import com.flab.inqueue.security.jwt.JwtAuthenticationProvider
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val hmacAuthenticationProvider: HmacAuthenticationProvider,
    private val jwtAuthenticationProvider: JwtAuthenticationProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customAccessDenierHandler: CustomAccessDenierHandler
) {
    companion object {
        private val HMAC_AUTHENTICATION_REQUEST_MATCHER = AntPathRequestMatcher("/server/**")
        private val JWT_AUTHENTICATION_REQUEST_MATCHER = AntPathRequestMatcher("/client/**")
    }
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .cors().disable()
            .authorizeHttpRequests()
            .requestMatchers(HMAC_AUTHENTICATION_REQUEST_MATCHER).authenticated()
            .requestMatchers(JWT_AUTHENTICATION_REQUEST_MATCHER).authenticated()
            .anyRequest().permitAll()
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http
            .addFilterBefore(
                hmacSignatureAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterAfter(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            )

        http.exceptionHandling()
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDenierHandler)
        return http.build()
    }

    @Bean
    fun hmacSignatureAuthenticationFilter(): HmacSignatureAuthenticationFilter {
        val authenticationManager = ProviderManager(hmacAuthenticationProvider)
        return HmacSignatureAuthenticationFilter(authenticationManager, HMAC_AUTHENTICATION_REQUEST_MATCHER)
    }

    @Bean
    fun jwtAuthenticationFilter() : JwtAuthenticationFilter {
        val authenticationManager = ProviderManager(jwtAuthenticationProvider)
        return JwtAuthenticationFilter(authenticationManager, JWT_AUTHENTICATION_REQUEST_MATCHER)
    }

    @Profile("dev")
    @Bean
    fun devWebSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(PathRequest.toH2Console())
        }
    }

    @Profile("prod")
    @Bean
    fun prodWebSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        }
    }
}