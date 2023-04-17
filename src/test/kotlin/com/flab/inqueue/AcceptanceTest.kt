package com.flab.inqueue

import com.flab.inqueue.support.IntegrationTest
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration

@IntegrationTest
abstract class AcceptanceTest : TestContainer() {

    protected lateinit var given: RequestSpecification
    protected lateinit var givenWithDocument: RequestSpecification


    @BeforeEach
    fun setUpRequestSpecification(restDocumentation: RestDocumentationContextProvider, @LocalServerPort port: Int) {
        given = RestAssured.given(setUpRestDocs(restDocumentation)).port(port)
    }

    companion object {
        @JvmStatic
        private val mySQLContainer = MySQLContainer("mysql:8.0.23").withDatabaseName("test-db")
        init {
            mySQLContainer.start()
        }
    }

    private fun setUpRestDocs(restDocumentation: RestDocumentationContextProvider): RequestSpecification? {
        return RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .build()
    }
}

const val REST_DOCS_DOCUMENT_IDENTIFIER = "{class_name}/{method_name}/"