package com.flab.inqueue.security

import io.restassured.RestAssured
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Profile

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("security")
abstract class SecurityIntegrationTest {

    protected lateinit var given: RequestSpecification

    @BeforeEach
    fun setUpRequestSpecification(@LocalServerPort port: Int) {
        given = RestAssured.given().port(port)
    }
}