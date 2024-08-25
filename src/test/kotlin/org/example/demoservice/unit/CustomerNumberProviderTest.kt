package org.example.demoservice.unit

import org.example.demoservice.customer.CustomerNumberProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class CustomerNumberProviderTest {

    private val customerNumberProvider = CustomerNumberProvider()

    @Test
    fun customerNumberShouldHaveCorrectLength() {
        // When
        val customerNumber = customerNumberProvider.nextCustomerNumber()

        // Then
        assertEquals(5, customerNumber.length, "Customer number length should be 5 characters")
    }

    @Test
    fun customerNumberShouldBeAlphanumeric() {
        // When
        val customerNumber = customerNumberProvider.nextCustomerNumber()

        // Then
        assertTrue(customerNumber.all { it.isLetterOrDigit() }, "Customer number should only contain alphanumeric characters")
    }

    @RepeatedTest(100)
    fun nextCustomerNumberShouldGenerateUniqueNumbers() {
        // When
        val customerNumber1 = customerNumberProvider.nextCustomerNumber()
        val customerNumber2 = customerNumberProvider.nextCustomerNumber()

        // Then
        assertNotEquals(customerNumber1, customerNumber2, "Customer numbers should be unique")
    }
}
