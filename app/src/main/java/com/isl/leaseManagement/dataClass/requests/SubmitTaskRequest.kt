package com.isl.leaseManagement.dataClass.requests

data class SubmitTaskRequest(
    var data: SubmitTaskData,
    var processId: Int?
) {
    data class SubmitTaskData(
        var accountNumber: String?=null,
        var additionalDocuments: List<Document?>?=null,
        var documents: List<Document?>?=null,
        var paymentMethod: String?=null,
        var rentVATExpiryDate: String?=null,
        var sadadBillerCode: Int?=null,
        var sadadExpiryDate: String?=null
    ) {

        data class Document(
            var docId: String?
        )
    }
}