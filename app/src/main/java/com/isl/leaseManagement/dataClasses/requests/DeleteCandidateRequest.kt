package com.isl.leaseManagement.dataClasses.requests

import com.isl.itower.MyApp
import com.isl.leaseManagement.utils.Utilities

data class DeleteCandidateRequest(
    val selectedProperties: List<DeleteCandidate?>?,
    val latitude: String? = Utilities.getLatitude(MyApp.getAppContext()),
    val longitude: String? = Utilities.getLongitude(MyApp.getAppContext()),
    val requestId: String?,
    val source: String?="",
    val timestamp: String?=""
) {
    data class DeleteCandidate(
        val candidateId: Int?,
        val propertyId: String?
    )
}