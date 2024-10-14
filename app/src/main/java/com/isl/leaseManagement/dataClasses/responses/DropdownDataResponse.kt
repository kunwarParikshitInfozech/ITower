package com.isl.leaseManagement.dataClasses.responses

class DropdownDataResponse : ArrayList<DropdownDataResponse.DropdownDataResponseItem>() {
    data class DropdownDataResponseItem(
        val bankId: Int?,
        val bankName: String?,
        val localValue: String?,
        val paramId: String?,
        val paramType: String?,
        val paramValue: String?
    )

    fun getDropdownsListByParamType(paramType: String): DropdownDataResponse {
        val filteredList = this.filter { it.paramType == paramType }

        // Create a new DropdownDataResponse and add the initial entry
        return DropdownDataResponse().apply {
            add(DropdownDataResponseItem(
                bankId = null,
                bankName = null,
                localValue = null,
                paramId = "Choose an option", // Set the initial paramId
                paramType = null,
                paramValue = null
            ))
            addAll(filteredList) // Add the filtered items after the initial entry
        }
    }
}