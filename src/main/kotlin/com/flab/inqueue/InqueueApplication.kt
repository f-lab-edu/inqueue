package com.flab.inqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication
class InqueueApplication

fun main(args: Array<String>) {
    runApplication<InqueueApplication>(*args)
}
