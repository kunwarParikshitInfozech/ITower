package com.isl.leaseManagement.dataClasses.requests

data class DeleteDocumentRequest(
    val latitude: Int? = 0,
    val longitude: Int? = 0,
    val requestId: String?,
    val tagName: String?,
    val taskId: Int?,
    val timestamp: String? = ""
)