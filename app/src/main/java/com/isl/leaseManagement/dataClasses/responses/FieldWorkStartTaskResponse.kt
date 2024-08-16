package com.isl.leaseManagement.dataClasses.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Entity
@TypeConverters(
    FieldWorkAdditionalDocumentConverter::class,
    FieldWorkDocumentConverter::class,
    FieldWorkDataConverter::class
)
data class FieldWorkStartTaskResponse(
    @PrimaryKey
    var taskId: Int = 0,    //task id is used for room,and not fetched by API
    val additionalDocuments: List<FieldWorkAdditionalDocument>?,
    @TypeConverters(FieldWorkDataConverter::class)
    val data: FieldWorkData?,
    val processId: Int?
) {
    data class FieldWorkAdditionalDocument(
        val content: String?,
        val fileName: String?,
        val tagName: String?,
        val docId: String?
    )

    data class FieldWorkData(
        var accountNumber: String?,
        var applicationSumissionDate: String?,
        var baladiyaApplicationSubmitted: String?,   //spinner
        var baladiyaName: String?,         //spinner
        var baladiyaId: String?,
        var baladiyaPermitAcquired: String?,   //spinner
        var baladiyaRequestNumber: String?,
        var documents: List<FieldWorkDocument?>?,
        var taskFlag: String?,    //this is for saving data to API, 3 forms 3 tags on baladiya
        var followupWithBaladiyaCompleted: String?,  //spinner
        var paymentPeriodDays: Int?,
        var sadadBillerCode: Int?,
        var trackingNumber: String?,
        var remarks: String?,
        var isSecondFormSubmitted: Boolean = false    //not from API, just to check if 2nd form is submitted or not
    ) {
        data class FieldWorkDocument(
            val content: String?,          // getting from start API
            val fileName: String?,        // getting from start API
            val tagName: String?,        // getting from start API
            val docId: String?,         //this is only used in save response for baladiya, all other 3 in this dta class will be null when saving case

            var documentTypeName: String? = "",   //these 3 fields are only required to show document in phone, so for saving to room only, not to start or save API
            var docSize: String? = "Unknown",
            var dateOfSaving: String = getCurrentDateInCustomFormat()
        )
    }
}

private fun getCurrentDateInCustomFormat(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(calendar.time)
}

class FieldWorkAdditionalDocumentConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<FieldWorkStartTaskResponse.FieldWorkAdditionalDocument>? {
        return gson.fromJson(
            value,
            object :
                TypeToken<List<FieldWorkStartTaskResponse.FieldWorkAdditionalDocument>>() {}.type
        )
    }

    @TypeConverter
    fun toString(list: List<FieldWorkStartTaskResponse.FieldWorkAdditionalDocument>?): String? {
        return gson.toJson(list)
    }
}

class FieldWorkDocumentConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<FieldWorkStartTaskResponse.FieldWorkData.FieldWorkDocument>? {
        return gson.fromJson(
            value,
            object :
                TypeToken<List<FieldWorkStartTaskResponse.FieldWorkData.FieldWorkDocument>>() {}.type
        )
    }

    @TypeConverter
    fun toString(list: List<FieldWorkStartTaskResponse.FieldWorkData.FieldWorkDocument>?): String? {
        return gson.toJson(list)
    }
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
