package com.flab.inqueue.security.hmacsinature

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "com.flab.inqueue.security",
        "com.flab.inqueue.domain.customer",
        "com.flab.inqueue.application",
    ]
)
@EnableJpaRepositories("com.flab.inqueue.domain.customer")
@EntityScan("com.flab.inqueue.domain.customer")
class HmacSignatureSecurityApp {
}