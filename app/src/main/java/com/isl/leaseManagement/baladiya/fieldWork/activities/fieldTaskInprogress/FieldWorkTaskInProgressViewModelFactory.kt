package com.isl.leaseManagement.baladiya.fieldWork.activities.fieldTaskInprogress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FieldWorkTaskInProgressViewModelFactory(private val repository: FieldWorkTaskInProgressRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FieldWorkTaskInProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FieldWorkTaskInProgressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}