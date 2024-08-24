package org.example.demoservice.customer.event

import org.example.demoservice.customer.dto.CustomerDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaCustomerEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, CustomerEvent>
) : CustomerEventProducer {
    @Value("\${spring.kafka.topic.customer-updates}")
    private lateinit var topic: String

    override fun sendCustomerCreatedEvent(customer: CustomerDTO) {
        kafkaTemplate.send(topic, CustomerEvent(customer, CustomerEventType.CREATED))
    }
}