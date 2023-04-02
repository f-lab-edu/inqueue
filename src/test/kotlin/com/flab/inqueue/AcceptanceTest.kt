package com.flab.inqueue

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension::class)
abstract class AcceptanceTest {

    protected lateinit var given: RequestSpecification

    @BeforeEach
    fun setUpRequestSpecification(restDocumentation: RestDocumentationContextProvider, @LocalServerPort port: Int) {
        given = RestAssured.given(setUpRestDocs(restDocumentation)).port(port)
    }

    companion object {
        @JvmStatic
        @Container
        private val mySQLContainer = MySQLContainer("mysql:8.0.23").withDatabaseName("test-db")
//        private val mySQLContainer = DockerComposeContainer(File("src/test/resources/docker-compose.yml"))
    }

    private fun setUpRestDocs(restDocumentation: RestDocumentationContextProvider): RequestSpecification? {
        return RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build()
    }
}

const val REST_DOCS_DOCUMENT_IDENTIFIER = "{class_name}/{method_name}/"