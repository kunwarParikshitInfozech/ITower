package com.isl.leaseManagement.baladiya.fieldWork.activities.fieldTaskInprogress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClasses.responses.SubmitBaladiyaFWRequest
import kotlinx.coroutines.launch

class FieldWorkTaskInProgressViewModel(private val repository: FieldWorkTaskInProgressRepository) :
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
