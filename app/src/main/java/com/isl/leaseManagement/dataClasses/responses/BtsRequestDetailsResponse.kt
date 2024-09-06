package com.isl.leaseManagement.dataClasses.responses

data class BTSRequestDetailsResponse(
    val bssRequestNumber: String?,
    val city: String?,
    val cityArabic: String?,
    val commercialAgreementNumber: String?,
    val customerSiteId: String?,
    val district: String?,
    val districtArabic: String?,
    val dpProposedTowerHeightForGfInMtr: String?,
    val expectedNumberOfColocationOnSite: String?,
    val expectedPackages: String?,
    val forecastDateForColocation: String?,
    val geoTypeLocationType: String?,
    val iprojectRequestId: String?,
    val latitudeOfNominalPoint: String?,
    val longitudeOfNominalPoint: String?,
    val potentialCustomer: String?,
    val projectName: String?,
    val region: String?,
    val remarks: String?,
    val requestType: String?,
    val requestingCustomer: String?,
    val searchRingRadius: String?,
    val siteId: String?,
    val version: String?,
    val vipRequest: String?
)