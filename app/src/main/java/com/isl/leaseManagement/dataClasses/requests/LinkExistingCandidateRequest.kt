package com.isl.leaseManagement.dataClasses.requests

import com.isl.itower.MyApp
import com.isl.leaseManagement.utils.Utilities

data class LinkExistingCandidateRequest(
    val latitude: String? = Utilities.getLatitude(MyApp.getAppContext()),
    val longitude: String? = Utilities.getLongitude(MyApp.getAppContext()),
    val requestId: String?,
    val selectedProperties: List<SelectedProperty?>?,
    val source: String? = "",
    val timestamp: String? = ""
) {
    data class SelectedProperty(
        val propertyId: String?,
        val candidateId: Int?,
    )
}
