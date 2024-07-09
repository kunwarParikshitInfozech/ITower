package com.isl.leaseManagement.activities.basicDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BasicDetailsViewModelFactory(private val repository: BasicDetailsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BasicDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BasicDetailsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}