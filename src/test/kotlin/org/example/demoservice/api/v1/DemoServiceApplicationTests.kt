package org.example.demoservice.api.v1

import org.bson.Document
import org.example.demoservice.DemoServiceApplication
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
    private lateinit var customerRestController: CustomerRestControllerV1

    @BeforeEach
    fun setUp() {
        mongoOperations.collectionNames.forEach {
            mongoOperations.getCollection(it).deleteMany(Document())
        }
    }

    @Test
    fun registerAndFindCustomer() {
        //given
        val tenantId = "test-tenant"
        val email = "email@example.com"

        //when
        val customer =
            customerRestController.registerCustomer(tenantId, RegistrationRequest(email))
        val foundCustomer = customerRestController.getCustomer(tenantId, customer.customerNumber)

        //then
        Assertions.assertEquals(customer, foundCustomer)
        Assertions.assertEquals(email, foundCustomer.email)
    }

    @Test
    fun registerAndFindCustomers() {
        //given
        val tenantId = "test-tenant"

        val email1 = "email1@example.com"
        val email2 = "email2@example.com"

        //when
        val customer1 = customerRestController.registerCustomer(tenantId, RegistrationRequest(email1))
        val customer2 = customerRestController.registerCustomer(tenantId, RegistrationRequest(email2))
        val customerList = customerRestController.getCustomers(tenantId, 0, 10)

        //then
        Assertions.assertNotNull(customerList)
        Assertions.assertEquals(0, customerList.page)
        Assertions.assertEquals(10, customerList.size)
        Assertions.assertEquals(2, customerList.totalElements)
        Assertions.assertEquals(1, customerList.totalPages)

        val customers = customerList.customers
        Assertions.assertEquals(2, customers.size)
        Assertions.assertTrue(customers.contains(customer1))
        Assertions.assertTrue(customers.contains(customer2))

        val foundCustomer1 = customers.first { it.customerNumber == customer1.customerNumber }
        Assertions.assertEquals(email1, foundCustomer1.email)

        val foundCustomer2 = customers.first { it.customerNumber == customer2.customerNumber }
        Assertions.assertEquals(email2, foundCustomer2.email)
    }
}
