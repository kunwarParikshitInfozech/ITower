package com.isl.leaseManagement.dataClasses.requests

data class UploadDocumentRequest(
    val content: String?,
    val fileName: String?,
    val latitude: Int?,
    val longitude: Int?,
    val requestId: String?,
    val tagName: String?,
    val timeStamp: String?,
    val userId: Int?
)
