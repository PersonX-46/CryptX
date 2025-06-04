package com.personx.cryptx.viewmodel.encryption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EncryptionViewModelFactory(
    private val repository: EncryptionViewModelRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EncryptionViewModel::class.java)) {
            return EncryptionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
