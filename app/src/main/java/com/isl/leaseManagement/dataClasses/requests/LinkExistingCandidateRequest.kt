package com.isl.leaseManagement.dataClasses.requests

data class LinkExistingCandidateRequest(
    val latitude: Int?=0,
    val longitude: Int?=0,
    val requestId: String?,
    val selectedProperties: List<SelectedProperty?>?,
    val source: String?="",
    val timestamp: String?=""
) {
    data class SelectedProperty(
        val propertyId: String?
    )
}
