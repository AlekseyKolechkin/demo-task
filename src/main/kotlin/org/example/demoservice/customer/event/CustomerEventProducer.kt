package org.example.demoservice.customer.event

import org.example.demoservice.customer.dto.CustomerDTO

interface CustomerEventProducer {
    fun sendCustomerCreatedEvent(customer: CustomerDTO)
}