package com.isl.leaseManagement.dataClass.otherDataClasses

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SaveAdditionalDocument(
    var taskId: Int,
    var fileName: String? = "Camera Image",
    var docSize: String? = "Unknown",
    var docContentString64: String? = "",
    var docId: String? = "",
    var dateOfSaving: String = getCurrentDateInCustomFormat()
)

fun getCurrentDateInCustomFormat(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(calendar.time)
}