package com.isl.leaseManagement.activities.startTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClass.requests.StartTaskRequest
import com.isl.leaseManagement.dataClass.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClass.responses.StartTaskResponse
import kotlinx.coroutines.launch

class StartTaskViewModel(private val repository: StartTaskRepository) : ViewModel() {

    fun startTask(
        successCallback: (StartTaskResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        taskId: Int,
        body: StartTaskRequest
    ) {
        viewModelScope.launch {
            repository.startTask(successCallback, errorCallback, taskId = taskId, body = body)
        }
    }

}
