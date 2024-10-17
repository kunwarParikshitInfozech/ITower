package com.isl.leaseManagement.dataClasses.requests

import com.isl.itower.MyApp
import com.isl.leaseManagement.utils.Utilities

data class UploadDocumentRequest(
    val content: String?,
    val fileName: String?,
    val latitude: String? = Utilities.getLatitude(MyApp.getAppContext()),
    val longitude: String? = Utilities.getLongitude(MyApp.getAppContext()),
    val requestId: String?,
    val tagName: String?,
    val timeStamp: String?,
    val userId: Int?
)
