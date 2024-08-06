package com.isl.leaseManagement.paymentProcess.activities.requestDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClasses.responses.RequestDetailsResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import kotlinx.coroutines.launch

class RequestDetailsViewModel(private val repository: RequestDetailsRepository) : ViewModel() {

    fun getTaskRequestDetails(
        successCallback: (RequestDetailsResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        requestId: String
    ) {
        viewModelScope.launch {
            repository.getRequestDetails(
                successCallback,
                errorCallback,
                requestId = requestId
            )
        }
    }

}
