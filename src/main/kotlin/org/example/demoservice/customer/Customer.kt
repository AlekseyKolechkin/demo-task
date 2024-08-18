package org.example.demoservice.customer

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "customers")
@TypeAlias("customer")
@CompoundIndex(name = "unique_tenantId_customerNumber", def = "{'tenantId': 1, 'customerNumber': 1}", unique = true)
data class Customer(
    @Id
    val id: String? = null,
    val tenantId: String,
    val customerNumber: String,
    val email: String,
    val name: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null
)
