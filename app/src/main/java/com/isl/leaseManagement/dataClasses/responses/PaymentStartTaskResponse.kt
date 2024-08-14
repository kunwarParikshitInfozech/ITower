package com.isl.leaseManagement.dataClasses.responses

data class PaymentStartTaskResponse(
    val data: StartTaskData?,
    val processId: Int?
) {
    data class StartTaskData(
        var accountNumber: String?,
        var sadadExpiryDate:String?,
        var rentVATExpiryDate:String?,
        val documents: List<StartTaskDocument?>?,
        var paymentMethod: String?,
        var sadadBillerCode: String?,
        var shouldUpdateSubmitDocFromStart: Int = 0     // 0 = default, 1 = update, 2 = ignore
    ) {
        data class StartTaskDocument(
            var content: String?,
            var fileName: String?,
            val tagName: String?
        )
    }
}