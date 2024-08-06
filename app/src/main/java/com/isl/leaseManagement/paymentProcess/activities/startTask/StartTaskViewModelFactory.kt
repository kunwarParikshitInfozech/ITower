package com.isl.leaseManagement.paymentProcess.activities.startTask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StartTaskViewModelFactory(private val repository: StartTaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StartTaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}