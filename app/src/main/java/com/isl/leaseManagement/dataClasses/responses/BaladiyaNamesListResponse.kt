package com.isl.leaseManagement.dataClasses.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.isl.leaseManagement.utils.AppConstants.DropDownTypes.baladiyaNameTypeDropDown

class BaladiyaNamesListResponse :
    ArrayList<BaladiyaNamesListResponse.BaladiyaNamesListResponseItem>() {
    @Entity
    data class BaladiyaNamesListResponseItem(
        @PrimaryKey
        val baladiyaId: Int?,
        val baladiyaName: String?,
        var dropDownType: String? = baladiyaNameTypeDropDown,    //for room, we are distinguishing dropdowns based on this param
        var localName: String? = ""    //for room, might use in future
    )
}