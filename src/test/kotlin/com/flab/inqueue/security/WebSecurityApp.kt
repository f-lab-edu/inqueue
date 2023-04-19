package com.flab.inqueue.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ConfigurationPropertiesScan("com.flab.inqueue")
@SpringBootApplication(
    scanBasePackages = [
        "com.flab.inqueue.security",
        "com.flab.inqueue.domain.customer",
        "com.flab.inqueue.application",
    ]
)
@EnableJpaRepositories("com.flab.inqueue.domain.customer")
@EntityScan("com.flab.inqueue.domain.customer")
class WebSecurityApp {
}