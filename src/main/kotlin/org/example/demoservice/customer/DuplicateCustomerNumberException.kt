package org.example.demoservice.customer

class DuplicateCustomerNumberException(
    retries: Int,
) : RuntimeException("Failed to generate unique customer number after $retries retries. Please try again.")
