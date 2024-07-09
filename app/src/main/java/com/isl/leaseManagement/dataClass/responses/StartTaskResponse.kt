package com.isl.leaseManagement.dataClass.responses

data class StartTaskResponse(
    val data: StartTaskData?,
    val processId: Int?
) {
    data class StartTaskData(
        val accountNumber: String?,
        val paymentMethod: String?,
        val sadadBillerCode: String?
    )
}