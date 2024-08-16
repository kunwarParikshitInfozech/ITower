package com.isl.leaseManagement.dataClasses.responses

data class SubmitBaladiyaFWRequest(
    val latitude: Int? = 0,
    val longitude: Int? = 0,
    val processId: Int? = 0,
    val requestId: String? = "",
    val source: String? = "",
    val timestamp: String? = "",
    val additionalDocuments: List<AdditionalDocument?>?

) {
    data class AdditionalDocument(
        val docId: String?
    )
}

