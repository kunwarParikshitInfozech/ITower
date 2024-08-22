package com.isl.leaseManagement.baladiya.uploadPermit.activities.uploadPermitCertificate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UploadBaladiyaPermitViewModelFactory(private val repository: UploadBaladiyaPermitRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadBaladiyaPermitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UploadBaladiyaPermitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}