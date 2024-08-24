package org.example.demoservice

import org.example.demoservice.customer.*
import org.example.demoservice.customer.dto.CreateCustomerDTO
import org.example.demoservice.customer.event.CustomerEventProducer
import org.example.demoservice.customer.exception.DuplicateCustomerNumberException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException

@SpringBootTest
class CustomerServiceTest {

    private val customerRepository: CustomerRepository = mock(CustomerRepository::class.java)
    private val customerNumberProvider: CustomerNumberProvider = mock(CustomerNumberProvider::class.java)
    private val customerEventProducer: CustomerEventProducer = mock(CustomerEventProducer::class.java)

    private val customerService = CustomerService(customerRepository, customerNumberProvider, customerEventProducer)

    @Value("\${customer.number.max.retries}")
    private var maxRetries: Int = 3

    @Test
    fun throwsDuplicateCustomerNumberException() {
        // given
        val newCustomer = CreateCustomerDTO("tenantId", "email", "name", "address", "phoneNumber")
        `when`(customerNumberProvider.nextCustomerNumber()).thenReturn("customerNumber")
        `when`(customerRepository.save(any(Customer::class.java))).thenThrow(DuplicateKeyException::class.java)

        // when
        val exception = assertThrows(DuplicateCustomerNumberException::class.java) {
            customerService.registerCustomer(newCustomer)
        }

        // then
        assertEquals("Failed to generate unique customer number after $maxRetries retries. Please try again.", exception.message)
    }

    @Test
    fun retriesSuccessfullyIfDuplicateCustomerNumberEncountered() {
        // given
        val newCustomer = CreateCustomerDTO("tenantId", "email", "name", "address", "phoneNumber")
        `when`(customerNumberProvider.nextCustomerNumber()).thenReturn("customerNumber1", "customerNumber2")
        `when`(customerRepository.save(any(Customer::class.java)))
            .thenThrow(DuplicateKeyException::class.java)
            .thenReturn(Customer("id", "tenantId", "customerNumber2", "email", "name", "address", "phoneNumber"))

        // when
        val result = customerService.registerCustomer(newCustomer)

        // then
        verify(customerRepository, times(2)).save(any(Customer::class.java))
        assertEquals("customerNumber2", result.customerNumber)
    }
}