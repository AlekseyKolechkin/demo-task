package org.example.demoservice.customer.event

import org.example.demoservice.customer.dto.CustomerDTO

data class CustomerEvent(
    val customer: CustomerDTO,
    val eventType: CustomerEventType
)