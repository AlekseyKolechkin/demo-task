package org.example.demoservice.api.v1.model

import org.example.demoservice.customer.dto.CreateCustomerDTO
import org.example.demoservice.customer.dto.CustomerDTO

data class ApiCustomerList(
    val customers: List<ApiCustomer>,
)

data class ApiCustomer(
    val customerNumber: String,
    val email: String,
)

data class RegistrationRequest(
    val email: String,
)

fun RegistrationRequest.toCreateCustomerDTO(tenantId: String) = CreateCustomerDTO(
    tenantId = tenantId,
    email = email
)

fun CustomerDTO.toApi() = ApiCustomer(
    customerNumber = customerNumber,
    email = email
)

fun List<CustomerDTO>.toApi() = ApiCustomerList(
    customers = map {
        it.toApi()
    }
)
