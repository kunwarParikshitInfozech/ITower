package com.isl.leaseManagement.dataClasses.responses


data class UpdatePropertyResponse(
    val data: DataUpdateProperty?,
    val flag: String?
) {
    data class DataUpdateProperty(
        val candidateId: Int?,
        val propertyId: String?
    )
}
