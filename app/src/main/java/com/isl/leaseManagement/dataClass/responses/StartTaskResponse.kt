package com.isl.leaseManagement.dataClass.responses

data class StartTaskResponse(
    val data: StartTaskData?,
    val processId: Int?
) {
    data class StartTaskData(
        var accountNumber: String?,
        val documents: List<StartTaskDocument?>?,
        var paymentMethod: String?,
        var sadadBillerCode: String?,
        var shouldUpdateSubmitDocFromStart: Boolean = true     // this is not for api, just to allow only first time saving doc from start to submit
    ) {
        data class StartTaskDocument(
            var content: String?,
            var fileName: String?,
            val tagName: String?
        )
    }
}