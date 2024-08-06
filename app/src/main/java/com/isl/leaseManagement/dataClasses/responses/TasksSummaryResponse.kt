package com.isl.leaseManagement.dataClasses.responses

data class TasksSummaryResponse(
    val taskStatus: String?,
    val today: Int?,
    val total: Int?
)