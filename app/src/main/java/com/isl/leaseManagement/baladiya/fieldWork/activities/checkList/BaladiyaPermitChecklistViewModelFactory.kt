package com.isl.leaseManagement.baladiya.fieldWork.activities.checkList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BaladiyaPermitChecklistViewModelFactory(private val repository: BaladiyapermitChecklistRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaladiyaPermitChecklistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BaladiyaPermitChecklistViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}