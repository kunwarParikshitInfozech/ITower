package com.isl.leaseManagement.paymentProcess.activities.addAdditionalDoc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClasses.requests.UploadDocumentRequest
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClasses.responses.UploadDocumentResponse
import kotlinx.coroutines.launch

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
