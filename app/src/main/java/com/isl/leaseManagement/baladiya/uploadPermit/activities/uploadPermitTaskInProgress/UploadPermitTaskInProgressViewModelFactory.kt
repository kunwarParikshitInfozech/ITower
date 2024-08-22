package com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitTaskInProgress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UploadPermitTaskInProgressViewModelFactory(private val repository: UploadPermitTaskInProgressRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadPermitTaskInProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UploadPermitTaskInProgressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}