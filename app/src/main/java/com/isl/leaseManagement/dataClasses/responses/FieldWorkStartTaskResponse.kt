package com.isl.leaseManagement.dataClasses.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocumentListConverter

@Entity
@TypeConverters(
    FieldWorkDataConverter::class,
    SaveAdditionalDocumentListConverter::class
)
data class FieldWorkStartTaskResponse(
    @PrimaryKey
    var taskId: Int = 0,    //task id is used for room,and not fetched by API
    @TypeConverters(SaveAdditionalDocumentListConverter::class)
    var additionalDocuments: List<SaveAdditionalDocument>?,
    @TypeConverters(FieldWorkDataConverter::class)
    val data: FieldWorkData?,
    val processId: Int?
) {
    data class FieldWorkData(
        var accountNumber: String?,
        var applicationSumissionDate: String?,
        var baladiyaApplicationSubmitted: String?,   //spinner
        var baladiyaName: String?,         //spinner
        var baladiyaId: String?,
        var baladiyaPermitAcquired: String?,   //spinner
        var baladiyaRequestNumber: String?,
        @TypeConverters(SaveAdditionalDocumentListConverter::class)
        var documents: List<SaveAdditionalDocument?>?,
        var taskFlag: String?,    //this is for saving data to API, 3 forms 3 tags on baladiya
        var followupWithBaladiyaCompleted: String?,  //spinner
        var paymentPeriodDays: Int?,
        var sadadBillerCode: Int?,
        var trackingNumber: String?,
        var remarks: String?,
        var isSecondFormSubmittedOnFieldWork: Boolean = false,    //not from API, just to check if 2nd form is submitted or not
    )
}


class FieldWorkDataConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): FieldWorkStartTaskResponse.FieldWorkData? {
        return gson.fromJson(
            value,
            object : TypeToken<FieldWorkStartTaskResponse.FieldWorkData>() {}.type
        )
    }

    @TypeConverter
    fun toString(data: FieldWorkStartTaskResponse.FieldWorkData?): String? {
        return gson.toJson(data)
    }
}
