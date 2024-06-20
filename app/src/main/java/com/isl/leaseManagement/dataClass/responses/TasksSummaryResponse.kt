package com.isl.leaseManagement.dataClass.responses

data class TasksSummaryResponse(
    val taskStatus: String?,
    val today: Int?,
    val total: Int?
)