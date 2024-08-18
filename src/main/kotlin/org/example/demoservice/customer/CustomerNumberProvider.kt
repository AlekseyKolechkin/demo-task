package org.example.demoservice.customer

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class CustomerNumberProvider {

    companion object {
        private const val NUMBER_LENGTH = 5
        private const val CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    }

    fun nextCustomerNumber(): String {
        return (1..NUMBER_LENGTH)
            .map { Random.nextInt(0, CHAR_POOL.length) }
            .map(CHAR_POOL::get)
            .joinToString("")
    }
}
