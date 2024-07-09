package com.isl.leaseManagement.dataClass.requests

data class StartTaskRequest(
    val latitude: Int?,
    val longitude: Int?,
    val requestId: String?,
    val source: String?,
    val timestamp: String?
)