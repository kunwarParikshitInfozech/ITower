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
    SaveAdditionalDocumentListConverter::class
)
data class BtsCaptureCandidateStartResponse(
    @PrimaryKey
    var taskId: Int = 0,    //task id is used for room,and not fetched by API
    @TypeConverters(SaveAdditionalDocumentListConverter::class)
    val additionalDocuments: List<SaveAdditionalDocument?>?,
    @TypeConverters(ExistingCandidateListResponseConverter::class)
    val data: ExistingCandidateListResponse?,
    val processId: Int?,
    val reqDistrictName: String?,
    val reqDistrictId: Int?,
    val reqCityName: String?,
    val reqCityId: Int?,
    val latitudeOfNominalPoint: String?,
    val longitudeOfNominalPoint: String?
)
