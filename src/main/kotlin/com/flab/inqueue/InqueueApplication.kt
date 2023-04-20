package com.flab.inqueue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class InqueueApplication

fun main(args: Array<String>) {
    runApplication<InqueueApplication>(*args)
}
