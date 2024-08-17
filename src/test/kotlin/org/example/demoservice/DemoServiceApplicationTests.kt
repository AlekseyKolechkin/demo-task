package org.example.demoservice

import org.bson.Document
import org.example.demoservice.api.v1.CustomerRestController
import org.example.demoservice.api.v1.model.RegistrationRequest
import org.example.demoservice.testconfig.MongoDBTestContainerConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ContextConfiguration(
    classes = [MongoDBTestContainerConfig::class]
)
@SpringBootTest(
    classes = [DemoServiceApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
class DemoServiceApplicationTests {

    @Autowired
    private lateinit var mongoOperations: MongoOperations

    @Autowired
    private lateinit var customerRestController: CustomerRestController

    @BeforeEach
    fun setUp() {
        mongoOperations.collectionNames.forEach {
            mongoOperations.getCollection(it).deleteMany(Document())
        }
    }

    @Test
    fun registerAndFindCustomer() {
        //given
        val email = "email@example.com"
        val name = "Bruce Robertson"
        val address = "221B Baker Str."
        val phoneNumber = "555-2368"
        val tenantId = "test-tenant"

        //when
        val customer = customerRestController.registerCustomer(tenantId, RegistrationRequest(email, name, address, phoneNumber))
        val foundCustomer = customerRestController.getCustomer(tenantId, customer.customerNumber)

        //then
        Assertions.assertEquals(customer, foundCustomer)
        Assertions.assertEquals(email, foundCustomer.email)
        Assertions.assertEquals(name, foundCustomer.name)
        Assertions.assertEquals(address, foundCustomer.address)
        Assertions.assertEquals(phoneNumber, foundCustomer.phoneNumber)
    }

    @Test
    fun registerAndFindCustomers() {
        //given
        val tenantId = "test-tenant"

        val email1 = "email1@example.com"
        val name1 = "Bruce Robertson"
        val address1 = "221B Baker Str."
        val phoneNumber1 = "555-2368"

        val email2 = "email2@example.com"
        val name2 = "John Doe"
        val address2 = "742 Evergreen Terrace"
        val phoneNumber2 = "555-1234"

        //when
        val customer1 = customerRestController.registerCustomer(tenantId, RegistrationRequest(email1, name1, address1, phoneNumber1))
        val customer2 = customerRestController.registerCustomer(tenantId, RegistrationRequest(email2, name2, address2, phoneNumber2))
        val customers = customerRestController.getCustomers(tenantId).customers

        //then
        Assertions.assertTrue(customers.contains(customer1))
        Assertions.assertTrue(customers.contains(customer2))

        val foundCustomer1 = customers.first { it.customerNumber == customer1.customerNumber }
        Assertions.assertEquals(email1, foundCustomer1.email)
        Assertions.assertEquals(name1, foundCustomer1.name)
        Assertions.assertEquals(address1, foundCustomer1.address)
        Assertions.assertEquals(phoneNumber1, foundCustomer1.phoneNumber)

        val foundCustomer2 = customers.first { it.customerNumber == customer2.customerNumber }
        Assertions.assertEquals(email2, foundCustomer2.email)
        Assertions.assertEquals(name2, foundCustomer2.name)
        Assertions.assertEquals(address2, foundCustomer2.address)
        Assertions.assertEquals(phoneNumber2, foundCustomer2.phoneNumber)
    }
}
