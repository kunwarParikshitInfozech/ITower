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
    BtsCaptureCandidateStartDataConverter::class,
    SaveAdditionalDocumentListConverter::class
)
data class BtsCaptureCandidateStartResponse(
    @PrimaryKey
    var taskId: Int = 0,    //task id is used for room,and not fetched by API
    @TypeConverters(SaveAdditionalDocumentListConverter::class)
    val additionalDocuments: List<SaveAdditionalDocument?>?,
    @TypeConverters(BtsCaptureCandidateStartDataConverter::class)
    val data: BtsCaptureCandidateStartData?,
    val processId: Int?
) {
    data class BtsCaptureCandidateStartData(
        val candidates: List<Candidate?>?
    ) {
        data class Candidate(
            val candidateId: Int?,
            val candidateStatus: String?,
            val landlordId: String?,
            val landlordName: String?,
            val propertyCity: String?,
            val propertyDistrict: String?,
            val propertyId: String?,
            val remarks: String?
        )
    }
}

class BtsCaptureCandidateStartDataConverter {
    private val gson = Gson()
    @TypeConverter
    fun fromString(value: String?): BtsCaptureCandidateStartResponse.BtsCaptureCandidateStartData? {
        return gson.fromJson(
            value,
            object :
                TypeToken<BtsCaptureCandidateStartResponse.BtsCaptureCandidateStartData>() {}.type
        )
    }
    @TypeConverter
    fun toString(data: BtsCaptureCandidateStartResponse.BtsCaptureCandidateStartData?): String? {
        return gson.toJson(data)
    }
}