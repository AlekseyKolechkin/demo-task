package org.example.demoservice.customer

@Suppress("serial")
class CustomerNotFoundException(
    tenantId: String,
    customerNumber: String,
) : RuntimeException("Customer with tenantId=$tenantId and customerNumber=$customerNumber not found")
