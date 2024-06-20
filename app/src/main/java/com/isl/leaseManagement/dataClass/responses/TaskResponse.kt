package com.isl.leaseManagement.dataClass.responses

data class TaskResponse(
    val requestId: String?,
    val siteId: String? = null,
    val customerSiteId: String? = null,
    val taskName: String?,
    val taskStatus: String?,
    val requestPriority: String?,
    val forecastStartDate: String? = null,
    val forecastEndDate: String? = null,
    val actualStartDate: String?,
    val slaDuration: Int? = null,
    val slaUnit: String? = null,
    val processName: String?,
    val processId: Int?,
    val taskId: Int?,
    val requestStatus: String?,
    val slaStatus: String?,
    val requester: String? = null,
    val region: String?,
    val district: String?,
    val city: String?
)
