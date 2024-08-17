package org.example.demoservice.api.v2.model

import org.example.demoservice.customer.dto.CreateCustomerDTO
import org.example.demoservice.customer.dto.CustomerDTO
import org.springframework.data.domain.Page

data class ApiCustomerList(
    val customers: List<ApiCustomer>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
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

fun Page<CustomerDTO>.toApi() = ApiCustomerList(
    customers = content.map {
        it.toApi()
    },
    page = number,
    size = size,
    totalElements = totalElements,
    totalPages = totalPages
)