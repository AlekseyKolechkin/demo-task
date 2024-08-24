package org.example.demoservice

import org.example.demoservice.api.v1.model.ApiErrorResponse
import org.example.demoservice.customer.event.CustomerEventProducer
import org.example.demoservice.testconfig.MongoDBTestContainerConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@ContextConfiguration(
    classes = [MongoDBTestContainerConfig::class]
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GlobalExceptionHandlerTest {

    @MockBean
    private lateinit var customerEventProducer: CustomerEventProducer

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun handleCustomerNotFoundException() {
        //given
        val tenantId = "test-tenant"
        val nonExistentCustomerNumber = "999"

        //when
        val response: ResponseEntity<ApiErrorResponse> = restTemplate.getForEntity(
            "/api/v1/customers/$tenantId/$nonExistentCustomerNumber",
            ApiErrorResponse::class.java
        )

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNotNull(response.body)
        val errorResponse = response.body!!
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.status)
        assertEquals(HttpStatus.NOT_FOUND.reasonPhrase, errorResponse.error)
        assertEquals("Customer with tenantId=$tenantId and customerNumber=$nonExistentCustomerNumber not found", errorResponse.message)
        assertEquals("/api/v1/customers/$tenantId/$nonExistentCustomerNumber", errorResponse.path)
    }
}