package com.flab.inqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
@ConfigurationPropertiesScan
class InqueueApplication

fun main(args: Array<String>) {
    runApplication<InqueueApplication>(*args)
}
