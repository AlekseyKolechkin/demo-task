package org.example.demoservice.api.v1.model

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

fun Page<CustomerDTO>.toApi() = ApiCustomerList(
    customers = content.map {
        it.toApi()
    },
    page = number,
    size = size,
    totalElements = totalElements,
    totalPages = totalPages
)
