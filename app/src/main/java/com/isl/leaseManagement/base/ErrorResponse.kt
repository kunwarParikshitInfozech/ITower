package com.isl.leaseManagement.base

data class ErrorResponse(
    val flag: String,
    val errors: List<ErrorDetail>
)

data class ErrorDetail(
    val code: String,
    val message: String
)