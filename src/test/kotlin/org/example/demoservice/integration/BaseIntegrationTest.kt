package org.example.demoservice.integration

import org.bson.Document
import org.example.demoservice.testconfig.MongoDBTestContainerConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
abstract class BaseIntegrationTest {

    @Autowired
    protected lateinit var mongoOperations: MongoOperations

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @BeforeEach
    fun setUp() {
        mongoOperations.collectionNames.forEach {
            mongoOperations.getCollection(it).deleteMany(Document())
        }
    }
}