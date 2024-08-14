package com.isl.leaseManagement.common.activities.startTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClasses.requests.StartTaskRequest
import com.isl.leaseManagement.dataClasses.responses.FieldWorkStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.PaymentStartTaskResponse
import com.isl.leaseManagement.dataClasses.responses.SingleMessageResponse
import kotlinx.coroutines.launch

class StartTaskViewModel(private val repository: StartTaskRepository) : ViewModel() {

    fun startTaskForPayment(
        successCallback: (PaymentStartTaskResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: StartTaskRequest
    ) {
        viewModelScope.launch {
            repository.startTaskPayment(
                successCallback,
                errorCallback,
                taskId = taskId,
                body = body
            )
        }
    }

    fun startTaskForFieldWork(
        successCallback: (FieldWorkStartTaskResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: StartTaskRequest
    ) {
        viewModelScope.launch {
            repository.startTaskFieldWork(
                successCallback,
                errorCallback,
                taskId = taskId,
                body = body
            )
        }
    }

}
