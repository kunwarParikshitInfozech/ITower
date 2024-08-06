package com.isl.leaseManagement.dataClasses.responses

data class FetchUserIdResponse(
    val loginId: String?,
    val primaryMobileNo: String?,
    val userId: Int?,
    val userName: String?
)