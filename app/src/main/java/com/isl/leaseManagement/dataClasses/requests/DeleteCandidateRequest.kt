package com.isl.leaseManagement.dataClasses.requests

data class DeleteCandidateRequest(
    val selectedProperties: List<DeleteCandidate?>?,
    val latitude: Int?=0,
    val longitude: Int?=0,
    val requestId: String?,
    val source: String?="",
    val timestamp: String?=""
) {
    data class DeleteCandidate(
        val candidateId: Int?,
        val propertyId: String?
    )
}