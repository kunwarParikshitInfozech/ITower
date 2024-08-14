package com.isl.leaseManagement.baladiya.fieldWork.activities.submit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SubmitBaladiyaRequestViewModelFactory(private val repository: SubmitBaladiyaRequestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubmitBaladiyaRequestViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SubmitBaladiyaRequestViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}