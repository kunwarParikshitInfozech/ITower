package com.isl.leaseManagement.activities.addAdditionalDoc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClass.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClass.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClass.responses.StartTaskResponse
import com.isl.leaseManagement.dataClass.responses.UploadDocumentResponse
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class AddAdditionalDocumentViewModel(private val repository: AddAdditionalDocumentRepository) : ViewModel() {

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
