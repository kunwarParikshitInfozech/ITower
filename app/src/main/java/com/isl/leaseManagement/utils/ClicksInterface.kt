package com.isl.leaseManagement.utils

import com.isl.leaseManagement.dataClass.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClass.responses.TaskResponse

object ClickInterfaces {
    interface MyTasks {
        fun myTaskClicked(taskResponse: TaskResponse)
    }

    interface AddAdditionalDocument {
        fun deleteDocument(saveAdditionalDocument: SaveAdditionalDocument)
    }

    interface AdditionalDocumentList {
        fun docInfo(saveAdditionalDocument: SaveAdditionalDocument)
        fun docDownload(saveAdditionalDocument: SaveAdditionalDocument)
    }
}