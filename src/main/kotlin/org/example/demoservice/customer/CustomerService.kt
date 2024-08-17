package org.example.demoservice.customer

import org.example.demoservice.customer.dto.CreateCustomerDTO
import org.example.demoservice.customer.dto.CustomerDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val customerNumberProvider: CustomerNumberProvider,
) {

    fun registerCustomer(newCustomer: CreateCustomerDTO): CustomerDTO {
        val customerNumber = customerNumberProvider.nextCustomerNumber()
        val customer = newCustomer.toEntity(customerNumber)
        return customerRepository.save(customer).toDto()
    }

    fun getCustomers(tenantId: String, page: Int, size: Int): Page<CustomerDTO> {
        val pageable = PageRequest.of(page, size)
        return customerRepository.findAllByTenantId(tenantId, pageable).map { it.toDto() }
    }

    fun getCustomer(tenantId: String, customerNumber: String): CustomerDTO {
        return customerRepository.findByTenantIdAndCustomerNumber(tenantId, customerNumber)?.toDto()
            ?: throw CustomerNotFoundException(tenantId, customerNumber)
    }
}
