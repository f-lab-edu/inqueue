package com.flab.inqueue

import com.flab.inqueue.support.IntegrationTest
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration
import org.testcontainers.containers.MySQLContainer

@IntegrationTest
abstract class AcceptanceTest : TestContainer() {

    protected lateinit var given: RequestSpecification
    protected lateinit var givenWithDocument: RequestSpecification


    @BeforeEach
    fun setUpRequestSpecification(restDocumentation: RestDocumentationContextProvider, @LocalServerPort port: Int) {
        givenWithDocument = RestAssured.given(setUpRestDocs(restDocumentation)).port(port)
        given = RestAssured.given().port(port)
    }

    private fun setUpRestDocs(restDocumentation: RestDocumentationContextProvider): RequestSpecification? {
        return RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build()
    }
}

const val REST_DOCS_DOCUMENT_IDENTIFIER = "{class_name}/{method_name}/"