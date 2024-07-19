package com.isl.leaseManagement.dataClass.otherDataClasses

data class SaveAdditionalDocument(
    val taskId: Int,
    var fileName: String? = "Camera",
    var docSize: String? = "Unknown",
    var docContentString64: String? = "",
    var docId: String? = "",
)
