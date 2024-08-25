package org.example.demoservice.unit

import org.example.demoservice.config.CaffeineCacheConfig
import org.example.demoservice.customer.*
import org.example.demoservice.customer.dto.CreateCustomerDTO
import org.example.demoservice.customer.dto.CustomerDTO
import org.example.demoservice.customer.event.CustomerEventProducer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(classes = [CustomerService::class, CaffeineCacheConfig::class])
class CustomerCacheTest {

    @Autowired
    private lateinit var customerService: CustomerService

    @MockBean
    private lateinit var customerRepository: CustomerRepository

    @MockBean
    private lateinit var customerNumberProvider: CustomerNumberProvider

    @MockBean
    private lateinit var customerEventProducer: CustomerEventProducer

    @Test
    fun registerNewCustomerAndCheckCache() {
        //given
        val tenantId = "tenant1"
        val customerNumber = "12345"
        val createCustomerDTO = CreateCustomerDTO(tenantId, "test@example.com", "Test Name", "Test Address", "1234567890")
        val customerDTO = CustomerDTO(customerNumber, tenantId, "test@example.com", "Test Name", "Test Address", "1234567890")
        `when`(customerNumberProvider.nextCustomerNumber()).thenReturn(customerNumber)
        `when`(customerRepository.save(any(Customer::class.java))).thenReturn(createCustomerDTO.toEntity(customerNumber))

        //when
        val registeredCustomer = customerService.registerCustomer(createCustomerDTO)

        //then
        assertEquals(customerDTO, registeredCustomer)
        verify(customerRepository, times(1)).save(any(Customer::class.java))

        val cachedCustomer = customerService.getCustomer(tenantId, customerNumber)
        assertEquals(customerDTO, cachedCustomer)
        verify(customerRepository, times(0)).findByTenantIdAndCustomerNumber(tenantId, customerNumber)
    }
}