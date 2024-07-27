package com.isl.leaseManagement.dataClass.responses

data class RequestDetailsResponse(
    val city: String?,
    val cityInArabic: String?,
    val commercialRegistrationNumber: String?,
    val district: String?,
    val districtInArabic: String?,
    val landlordEmail: String?,
    val landlordId: String?,
    val landlordName: String?,
    val landlordNameInArabic: String?,
    val landlordNationalIdNumber: String?,
    val landlordTelephone: Long?,
    val landlordType: String?,
    val isVATApplicable:String?
)