package org.example.demoservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.example.demoservice.customer.dto.CreateCustomerDTO
import org.example.demoservice.customer.event.CustomerEvent
import org.example.demoservice.customer.event.CustomerEventType
import org.example.demoservice.testconfig.KafkaTestContainerConfig
import org.example.demoservice.testconfig.MongoDBTestContainerConfig
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(
    classes = [MongoDBTestContainerConfig::class, KafkaTestContainerConfig::class]
)
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerEventKafkaTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var kafkaContainer: KafkaContainer

    @Value("\${spring.kafka.topic.customer-updates}")
    private lateinit var topic: String

    private val objectMapper = jacksonObjectMapper()

    @BeforeAll
    fun setUp() {
        kafkaContainer.start()
        kafkaContainer.waitingFor(Wait.forListeningPort())
    }

    @AfterAll
    fun tearDown() {
        kafkaContainer.stop()
    }

    @Test
    fun checkEventPublishingAfterCustomerCreation() {
        //before
        val tenantId = "tenant1"
        val email = "test@example.com"
        val name = "John Doe"
        val address = "123 Main St"
        val phoneNumber = "123-456-7890"

        val createCustomerDTO = CreateCustomerDTO(
            tenantId = tenantId,
            email = email,
            name = name,
            address = address,
            phoneNumber = phoneNumber
        )

        //when
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v2/customers/$tenantId")
                .contentType("application/json")
                .content(ObjectMapper().writeValueAsString(createCustomerDTO))
        ).andExpect(MockMvcResultMatchers.status().isOk)

        //then
        val consumer = prepareConsumer()
        val records = consumer.poll(Duration.ofSeconds(10))

        assertThat(records.count()).isGreaterThan(0)
        val record = records.first()
        val message: CustomerEvent = objectMapper.readValue(record.value())

        assertThat(message.customer.customerNumber).isNotNull()
        assertThat(message.customer.tenantId).isEqualTo(tenantId)
        assertThat(message.customer.email).isEqualTo(email)
        assertThat(message.customer.name).isEqualTo(name)
        assertThat(message.customer.address).isEqualTo(address)
        assertThat(message.customer.phoneNumber).isEqualTo(phoneNumber)
        assertThat(message.eventType).isEqualTo(CustomerEventType.CREATED)
    }

    private fun prepareConsumer(): KafkaConsumer<String, String> {
        val consumerProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
            ConsumerConfig.GROUP_ID_CONFIG to "testGroup",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name
        )

        val consumer = KafkaConsumer<String, String>(consumerProps)
        consumer.subscribe(listOf(topic))
        return consumer
    }
}
