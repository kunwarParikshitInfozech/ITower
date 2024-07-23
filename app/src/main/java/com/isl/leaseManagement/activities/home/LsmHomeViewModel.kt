package com.isl.leaseManagement.activities.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClass.requests.FetchDeviceIDRequest
import com.isl.leaseManagement.dataClass.responses.FetchUserIdResponse
import com.isl.leaseManagement.dataClass.responses.SingleMessageResponse
import kotlinx.coroutines.launch

class LsmHomeViewModel(private val repository: LsmHomeRepository) : ViewModel() {

    fun fetchUserId(
        successCallback: (FetchUserIdResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        body: FetchDeviceIDRequest
    ) {
        viewModelScope.launch {
            repository.fetchDeviceID(successCallback, errorCallback, body = body)
        }
    }

}
