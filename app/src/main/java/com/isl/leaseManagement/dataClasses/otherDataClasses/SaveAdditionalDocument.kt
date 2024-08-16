package com.isl.leaseManagement.dataClasses.otherDataClasses

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SaveAdditionalDocument(
    var taskId: Int,
    var documentTypeName: String? = "",
    var fileName: String? = "No Document Selected",
    var docSize: String? = "Size",
    var docContentString64: String? = "",
    var docId: String? = "",
    var tagName: String? = "",
    var dateOfSaving: String? = getCurrentDateInCustomFormat()
)

private fun getCurrentDateInCustomFormat(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(calendar.time)
}