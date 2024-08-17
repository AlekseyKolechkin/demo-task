package org.example.demoservice.customer

import org.example.demoservice.customer.dto.CreateCustomerDTO
import org.example.demoservice.customer.dto.CustomerDTO
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

    fun getCustomers(tenantId: String): List<CustomerDTO> {
        return customerRepository.findAllByTenantId(tenantId).toDto()
    }

    fun getCustomer(tenantId: String, customerNumber: String): CustomerDTO {
        return customerRepository.findByTenantIdAndCustomerNumber(tenantId, customerNumber)?.toDto()
            ?: throw CustomerNotFoundException(tenantId, customerNumber)
    }
}
