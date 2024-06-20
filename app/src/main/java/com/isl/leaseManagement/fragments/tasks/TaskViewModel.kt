package com.isl.leaseManagement.fragments.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isl.leaseManagement.dataClass.responses.TaskResponse
import com.isl.leaseManagement.dataClass.responses.TasksSummaryResponse
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<TaskResponse>>()
    val tasks: LiveData<List<TaskResponse>> get() = _tasks

    private val _tasksSummary = MutableLiveData<List<TasksSummaryResponse>>()
    val tasksSummary: LiveData<List<TasksSummaryResponse>> get() = _tasksSummary

    fun fetchTasks() {
        viewModelScope.launch {
            repository.getTasks { taskList ->
                _tasks.postValue(taskList!!)
            }
        }
    }

    fun fetchTasksSummary() {
        viewModelScope.launch {
            repository.getTasksSummary { taskSummaryList ->
                _tasksSummary.postValue(taskSummaryList!!)
            }
        }
    }

}
