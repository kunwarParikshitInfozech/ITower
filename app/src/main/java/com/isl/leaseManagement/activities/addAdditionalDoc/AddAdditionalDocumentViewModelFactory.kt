package com.isl.leaseManagement.activities.addAdditionalDoc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddAdditionalDocumentViewModelFactory(private val repository: AddAdditionalDocumentRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddAdditionalDocumentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddAdditionalDocumentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}