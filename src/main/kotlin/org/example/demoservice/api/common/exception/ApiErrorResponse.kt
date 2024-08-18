package org.example.demoservice.api.common.exception
data class ApiErrorResponse(
    val status: Int,
    val error: String,
    val message: String?,
    val path: String
)