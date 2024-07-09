package com.isl.leaseManagement.dataClass.requests

data class SubmitTaskRequest(
    val data: SubmitTaskData?,
    val processId: Int?
) {
    data class SubmitTaskData(
        val accountNumber: String?=null,
        val additionalDocuments: List<Document?>?=null,
        val documents: List<Document?>?=null,
        val paymentMethod: String?=null,
        val rentVATExpiryDate: String?=null,
        val sadadBillerCode: Int?=null,
        val sadadExpiryDate: String?=null
    ) {

        data class Document(
            val docId: String?
        )
    }
}