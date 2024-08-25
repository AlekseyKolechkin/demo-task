package org.example.demoservice.api.v1

import org.example.demoservice.api.v1.model.ApiCustomer
import org.example.demoservice.api.v1.model.ApiCustomerList
import org.example.demoservice.api.common.exception.ApiErrorResponse
import org.example.demoservice.api.v1.model.RegistrationRequest
import org.example.demoservice.customer.event.CustomerEventProducer
import org.example.demoservice.api.BaseIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class CustomerApiV1Test : BaseIntegrationTest() {

    @MockBean
    private lateinit var customerEventProducer: CustomerEventProducer

    @Test
    fun registerAndFindCustomer() {
        //given
        val tenantId = "test-tenant"
        val registrationRequest = RegistrationRequest(
            email = "email@example.com"
        )

        //when
        val registrationResponse: ResponseEntity<ApiCustomer> = restTemplate.postForEntity(
            "/api/v1/customers/$tenantId",
            registrationRequest,
            ApiCustomer::class.java
        )
        val registeredCustomer = registrationResponse.body

        val getResponse: ResponseEntity<ApiCustomer> = restTemplate.getForEntity(
            "/api/v1/customers/$tenantId/${registeredCustomer?.customerNumber}",
            ApiCustomer::class.java
        )
        val foundCustomer = getResponse.body

        //then
        assertEquals(HttpStatus.OK, registrationResponse.statusCode)
        assertEquals(HttpStatus.OK, getResponse.statusCode)
        assertEquals(registeredCustomer, foundCustomer)
        assertEquals(registrationRequest.email, foundCustomer?.email)
    }

    @Test
    fun registerAndFindCustomers() {
        //given
        val tenantId = "test-tenant"

        val registrationRequest1 = RegistrationRequest(
            email = "email1@example.com",
        )

        val registrationRequest2 = RegistrationRequest(
            email = "email2@example.com"
        )

        //when
        val customer1 = restTemplate.postForEntity(
            "/api/v1/customers/$tenantId",
            registrationRequest1,
            ApiCustomer::class.java
        ).body

        val customer2 = restTemplate.postForEntity(
            "/api/v1/customers/$tenantId",
            registrationRequest2,
            ApiCustomer::class.java
        ).body

        val customerListResponse: ResponseEntity<ApiCustomerList> = restTemplate.getForEntity(
            "/api/v1/customers/$tenantId?page=0&size=10",
            ApiCustomerList::class.java
        )

        //then
        assertEquals(HttpStatus.OK, customerListResponse.statusCode)
        assertNotNull(customer1)
        assertNotNull(customer2)

        val customersList = customerListResponse.body!!
        assertEquals(0, customersList.page)
        assertEquals(10, customersList.size)
        assertEquals(2, customersList.totalElements)
        assertEquals(1, customersList.totalPages)

        val customers = customersList.customers
        assertEquals(2, customers.size)

        val foundCustomer1 = customers.first { it.customerNumber == customer1!!.customerNumber }
        assertEquals("email1@example.com", foundCustomer1.email)
        val foundCustomer2 = customers.first { it.customerNumber == customer2!!.customerNumber }
        assertEquals("email2@example.com", foundCustomer2.email)
    }

    @Test
    fun tryGetNonExistentCustomerReceiveError() {
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
