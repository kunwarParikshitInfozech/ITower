package com.isl.leaseManagement.dataClasses.responses

class LocationsListResponse : ArrayList<LocationsListResponse.LocationsListResponseItem>() {
    data class LocationsListResponseItem(
        val cityId: Int?,
        val cityName: String?,
        val districtId: Int?,
        val districtName: String?,
        val regionId: Int?,
        val regionName: String?,
        val regionLocalName: String?,
        val districtLocalName: String?,
        val cityLocalName: String?
    )
}
