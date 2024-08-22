package com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitTaskInProgress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClasses.responses.SubmitBaladiyaFWRequest
import kotlinx.coroutines.launch

class UploadPermitTaskInProgressViewModel(private val repository: UploadPermitTaskInProgressRepository) :
    ViewModel() {

    fun getBaladiyaName(
        successCallback: (BaladiyaNamesListResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit
    ) {
        viewModelScope.launch {
            repository.getBaladiyaName(
                successCallback,
                errorCallback,
            )
        }
    }

    fun submitBaladiyaFW(
        userId: String,
        taskId: Int,
        submitBaladiyaFWRequest: SubmitBaladiyaFWRequest,
        successCallback: (ApiSuccessFlagResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit
    ) {
        viewModelScope.launch {
            repository.submitBaladiyaFieldWork(
                userId = userId,
                taskId = taskId,
                submitBaladiyaFWRequest,
                successCallback,
                errorCallback,
            )
        }
    }


}
