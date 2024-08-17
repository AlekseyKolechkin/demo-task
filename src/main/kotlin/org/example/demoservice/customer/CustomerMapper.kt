package org.example.demoservice.customer

import org.example.demoservice.customer.dto.CreateCustomerDTO
import org.example.demoservice.customer.dto.CustomerDTO

fun List<Customer>.toDto(): List<CustomerDTO> = map { customer ->
    customer.toDto()
}

fun Customer.toDto(): CustomerDTO {
    return CustomerDTO(
        tenantId = tenantId,
        customerNumber = customerNumber,
        email = email,
        name = name,
        address = address,
        phoneNumber = phoneNumber
    )
}

fun CreateCustomerDTO.toEntity(customerNumber: String): Customer {
    return Customer(
        tenantId = tenantId,
        customerNumber = customerNumber,
        email = email,
        name = name,
        address = address,
        phoneNumber = phoneNumber
    )
}
