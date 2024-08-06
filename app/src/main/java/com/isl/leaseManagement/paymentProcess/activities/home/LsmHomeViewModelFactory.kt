package com.isl.leaseManagement.paymentProcess.activities.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LsmHomeViewModelFactory(private val repository: LsmHomeRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LsmHomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LsmHomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}