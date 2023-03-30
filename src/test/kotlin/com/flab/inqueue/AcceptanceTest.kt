package com.flab.inqueue

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(RestDocumentationExtension::class)
abstract class AcceptanceTest {

    protected lateinit var spec: RequestSpecification

    @BeforeEach
    fun setUpRestDocs(restDocumentation: RestDocumentationContextProvider) {
        spec = RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build()
    }

    companion object {
        @JvmStatic
        @Container
        private val mySQLContainer = MySQLContainer("mysql:8.0.23").withDatabaseName("test-db")
//        private val mySQLContainer = DockerComposeContainer(File("src/test/resources/docker-compose.yml"))
    }
}

const val REST_DOCS_DOCUMENT_IDENTIFIER = "{class_name}/{method_name}/"