package com.isl.leaseManagement.dataClasses.responses

data class LinkExistingCandidateResponse(
    val data: List<DataLinkExistingCandidate?>?,
    val flag: String?
) {
    data class DataLinkExistingCandidate(
        val candidateId: Int?,
        val isAccountValid: Boolean?,
        val isDelegateValid: Boolean?,
        val isDocValid: Boolean?,
        val isLandlordValid: Boolean?,
        val isPropertyValid: Boolean?,
        val propertyId: String?
    )
}