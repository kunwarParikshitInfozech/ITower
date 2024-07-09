package com.isl.leaseManagement.activities.requestDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RequestDetailsViewModelFactory(private val repository: RequestDetailsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RequestDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}