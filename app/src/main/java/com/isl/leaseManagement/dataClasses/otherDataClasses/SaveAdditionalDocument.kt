package com.isl.leaseManagement.dataClasses.otherDataClasses

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SaveAdditionalDocument(
    var taskId: Int,
    var documentTypeName: String? = "",
    var fileName: String? = "No Document Selected",
    var docSize: String? = "Size",
    var content: String? = "",
    var docId: String? = "",
    var tagName: String? = "",
    var dateOfSaving: String? = getCurrentDateInCustomFormat(),
    var isDocumentUploadedToAPI: Boolean = false,
    var isDocIdPermanent: Boolean = false
)

private fun getCurrentDateInCustomFormat(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(calendar.time)
}

class SaveAdditionalDocumentListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<SaveAdditionalDocument>? {
        val type = object : TypeToken<List<SaveAdditionalDocument?>?>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun toString(data: List<SaveAdditionalDocument?>?): String? {
        return gson.toJson(data)
    }
}

class SaveAdditionalDocumentConvertera {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): SaveAdditionalDocument? {
        return gson.fromJson(value, SaveAdditionalDocument::class.java)
    }

    @TypeConverter
    fun toString(data: SaveAdditionalDocument?): String? {
        return gson.toJson(data)
    }
}