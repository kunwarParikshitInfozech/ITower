package com.isl.leaseManagement.dataClasses.responses

import com.isl.itower.MyApp
import com.isl.leaseManagement.utils.Utilities

data class SubmitBaladiyaFWRequest(
    val latitude: String? = Utilities.getLatitude(MyApp.getAppContext()),
    val longitude: String? = Utilities.getLongitude(MyApp.getAppContext()),
    val processId: Int? = 0,
    val requestId: String? = "",
    val source: String? = "",
    val timestamp: String? = "",
    val additionalDocs: List<AdditionalDocument?>?

) {
    data class AdditionalDocument(
        val docId: String?
    )
}

