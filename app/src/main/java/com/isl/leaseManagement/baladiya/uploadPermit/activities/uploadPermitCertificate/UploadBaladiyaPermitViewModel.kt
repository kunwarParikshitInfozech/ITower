package com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitCertificate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClasses.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClasses.responses.BaladiyaNamesListResponse
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import kotlinx.coroutines.launch

class UploadBaladiyaPermitViewModel(private val repository: UploadBaladiyaPermitRepository) :
    ViewModel() {


    fun updateBaladiyaResponse(
        userId: String,
        taskId: Int,
        fieldWorkStartTaskResponse: FieldWorkStartTaskResponse,
        successCallback: (ApiSuccessFlagResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit
    ) {
        viewModelScope.launch {
            repository.updateBaladiyaApi(
                userId = userId,
                taskId = taskId,
                fieldWorkStartTaskResponse,
                successCallback,
                errorCallback,
            )
        }
    }

}
