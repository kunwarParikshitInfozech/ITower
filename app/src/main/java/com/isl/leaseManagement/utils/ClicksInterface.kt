package com.isl.leaseManagement.utils

import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClasses.responses.TaskResponse

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

    interface TwoOptionSelection {
        fun option1Selected()
        fun option2Selected()
    }
}