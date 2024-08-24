package org.example.demoservice.customer.exception

class DuplicateCustomerNumberException(
    retries: Int,
) : RuntimeException("Failed to generate unique customer number after $retries retries. Please try again.")
