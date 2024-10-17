package com.isl.leaseManagement.dataClasses.requests

import com.isl.itower.MyApp
import com.isl.leaseManagement.utils.Utilities

data class StartTaskRequest(
    val latitude: String? = Utilities.getLatitude(MyApp.getAppContext()),
    val longitude: String? = Utilities.getLongitude(MyApp.getAppContext()),
    val requestId: String?,
    val source: String?,
    val timestamp: String?
)

