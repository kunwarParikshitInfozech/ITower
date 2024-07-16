package com.isl.leaseManagement.dataClass.otherDataClasses


data class SaveAdditionalDocumentsArray(val arrayOfSaveAdditionalDocument:  List<SaveAdditionalDocument>?)
data class SaveAdditionalDocuments(
    val docName: String? = "",
    val docSize: String? = "",
    val docContentString64: String = "",  //will also act like identifier
    val docUploadTime: String = "",
)
