package com.isl.leaseManagement.dataClass.otherDataClasses

data class SaveAdditionalDocument(
    val docName: String? = "",
    val docSize: String? = "",
    val docContentString64: String = "",  //will also act like identifier
    val docUploadTime: String = "",
)
