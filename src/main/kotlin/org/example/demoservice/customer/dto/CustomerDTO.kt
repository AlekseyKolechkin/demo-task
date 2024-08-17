package org.example.demoservice.customer.dto

data class CustomerDTO(
    val customerNumber: String,
    val tenantId: String,
    val email: String,
    val name: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
)
