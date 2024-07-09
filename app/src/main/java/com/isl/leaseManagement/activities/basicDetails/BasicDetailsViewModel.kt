package com.isl.leaseManagement.activities.basicDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClass.requests.SubmitTaskRequest
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClass.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClass.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClass.responses.UploadDocumentResponse
import kotlinx.coroutines.launch

class BasicDetailsViewModel(private val repository: BasicDetailsRepository) : ViewModel() {

    fun submitTask(
        successCallback: (ApiSuccessFlagResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: SubmitTaskRequest
    ) {
        viewModelScope.launch {
            repository.submitTask(successCallback, errorCallback, taskId = taskId, body = body)
        }
    }

    fun uploadDocument(
        successCallback: (UploadDocumentResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: UploadDocumentRequest
    ) {
        viewModelScope.launch {
            repository.uploadDocument(successCallback, errorCallback, taskId = taskId, body = body)
        }
    }

}
