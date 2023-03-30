package com.flab.inqueue

import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class AcceptanceTest {

    companion object {
        @JvmStatic
        @Container
        private val mySQLContainer = MySQLContainer("mysql:8.0.23").withDatabaseName("test-db")
//        private val mySQLContainer = DockerComposeContainer(File("src/test/resources/docker-compose.yml"))
    }

}