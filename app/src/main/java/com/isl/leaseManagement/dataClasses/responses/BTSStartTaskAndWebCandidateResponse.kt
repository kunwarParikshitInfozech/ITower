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
    ExistingCandidateListResponseConverter::class,
    SaveAdditionalDocumentListConverter::class,
    BTSStartTaskAndWebCandidateResponse.CapturedCandidateFromWebConverter::class
)
data class BTSStartTaskAndWebCandidateResponse(
    @PrimaryKey
    var taskId: Int = 0,    // taskId is used for Room, and not fetched by API

    @TypeConverters(CapturedCandidateFromWebConverter::class)
    val data: CapturedCandidateFromWeb?,  // Single object based on your JSON structure

    val documents: List<SaveAdditionalDocument?>?,  // List of documents
    val latitudeOfNominalPoint: String?,
    val longitudeOfNominalPoint: String?,
    val processId: Int?,
    val reqCityId: Int?,
    val reqCityName: String?,
    val reqDistrictId: Int?,
    val reqDistrictName: String?
) {
    @TypeConverters(
        ExistingCandidateListResponseConverter::class,
        SaveAdditionalDocumentListConverter::class
    )
    data class CapturedCandidateFromWeb(
        @TypeConverters(SaveAdditionalDocumentListConverter::class)
        val additionalDocuments: List<SaveAdditionalDocument?>?,
        @TypeConverters(ExistingCandidateListResponseConverter::class)
        val candidates: List<ExistingCandidateListResponse.ExistingCandidateListResponseItem?>?,
        val processId: Int?,
        val reqDistrictName: String?,
        val reqDistrictId: Int?,
        val reqCityName: String?,
        val reqCityId: Int?,
        val latitudeOfNominalPoint: String?,
        val longitudeOfNominalPoint: String?
    )

    class CapturedCandidateFromWebConverter {

        @TypeConverter
        fun fromCapturedCandidateFromWeb(capturedCandidate: CapturedCandidateFromWeb?): String {
            return Gson().toJson(capturedCandidate)
        }

        @TypeConverter
        fun toCapturedCandidateFromWeb(data: String?): CapturedCandidateFromWeb? {
            val type = object : TypeToken<CapturedCandidateFromWeb>() {}.type
            return Gson().fromJson(data, type)
        }
    }
}