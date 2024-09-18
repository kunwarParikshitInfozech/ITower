package com.isl.leaseManagement.utils

import com.isl.leaseManagement.dataClasses.otherDataClasses.SaveAdditionalDocument
import com.isl.leaseManagement.dataClasses.responses.ExistingCandidateListResponse
import com.isl.leaseManagement.dataClasses.responses.TaskResponse

object ClickInterfaces {

    interface TriggerActionInterface {
        fun triggerAction()
    }

    interface CommonInterface {
        fun triggerWithString(string: String)
        fun triggerWithInt(int: Int)
    }

    interface CommonDataCallBack<T> {
        fun onSuccess(data: T?)
        fun onError(error: Throwable)
    }

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

    interface DistrictSelection {
        fun districtSelected(pair: Pair<Int?, String?>)
    }

    interface ExistingCandidateSelection {
        fun candidateClicked(selectedCandidateData: ExistingCandidateListResponse.ExistingCandidateListResponseItem)
    }
}