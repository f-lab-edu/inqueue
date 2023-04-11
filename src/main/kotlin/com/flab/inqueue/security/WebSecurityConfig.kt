package com.flab.inqueue.security

import com.flab.inqueue.security.hmacsinature.HmacSignatureFilter
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val hmacAuthenticationProvider: AuthenticationProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customAccessDenierHandler: CustomAccessDenierHandler
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .cors().disable()
            .authorizeHttpRequests()
            .requestMatchers("/v1/auth/**").authenticated()
            .anyRequest().permitAll()
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http
            .addFilterAfter(
                hmacSignatureFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            )
        http.exceptionHandling()
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDenierHandler)
        return http.build()
    }

    @Bean
    fun hmacSignatureFilter(): HmacSignatureFilter {
        val authenticationManager = ProviderManager(hmacAuthenticationProvider)
        return HmacSignatureFilter(authenticationManager)
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