package com.isl.leaseManagement.activities.loader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StartDataViewModelFactory(private val repository: StartDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StartDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}