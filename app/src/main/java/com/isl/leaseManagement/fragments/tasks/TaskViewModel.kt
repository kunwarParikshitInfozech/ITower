package com.isl.leaseManagement.fragments.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClass.responses.ApiSuccessFlagResponse
import com.isl.leaseManagement.dataClass.responses.SingleMessageResponse
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.dataClass.responses.TasksSummaryResponse
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasksSummary = MutableLiveData<List<TasksSummaryResponse>>()
    val tasksSummary: LiveData<List<TasksSummaryResponse>> get() = _tasksSummary

    fun fetchTasksSummary() {
        viewModelScope.launch {
            repository.getTasksSummary { taskSummaryList ->
                _tasksSummary.postValue(taskSummaryList!!)
            }
        }
    }

    fun fetchTasks(
        userid: Int,
        successCallback: (List<TaskResponse>?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        requestStatus: String?,
        taskStatus: String?,
        slaStatus: String?,
        requestPriority: String?
    ) {
        viewModelScope.launch {
            repository.getTasks(
                userid, successCallback, errorCallback, requestStatus = requestStatus,
                taskStatus = taskStatus,
                slaStatus = slaStatus,
                requestPriority = requestPriority
            )
        }
    }

    fun updateTaskStatus(
        successCallback: (ApiSuccessFlagResponse?) -> Unit,
        errorCallback: (SingleMessageResponse) -> Unit,
        taskId: Int,
        taskStatus: String,
        body: RequestBody
    ) {
        viewModelScope.launch {
            repository.updateTaskStatus(successCallback, errorCallback, taskId, taskStatus, body)
        }
    }

}
