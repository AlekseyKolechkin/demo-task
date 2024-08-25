package org.example.demoservice.api.v2.model

import org.example.demoservice.customer.dto.CustomerDTO
import org.example.demoservice.customer.dto.CreateCustomerDTO

data class ApiCustomerList(
    val customers: List<ApiCustomer>,
)

data class ApiCustomer(
    val customerNumber: String,
    val email: String,
    val name: String,
    val address: String,
    val phoneNumber: String
)

data class RegistrationRequest(
    val email: String,
    val name: String,
    val address: String,
    val phoneNumber: String
)

fun RegistrationRequest.toCreateCustomerDTO(tenantId: String) = CreateCustomerDTO(
    tenantId = tenantId,
    email = email,
    name = name,
    address = address,
    phoneNumber = phoneNumber
)

fun CustomerDTO.toApi() = ApiCustomer(
    customerNumber = customerNumber,
    email = email,
    name = name?: "",
    address = address?: "",
    phoneNumber = phoneNumber?: "",
)

fun List<CustomerDTO>.toApi() = ApiCustomerList(
    customers = map {
        it.toApi()
    }
)
