package com.personx.cryptx.viewmodel.decryption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DecryptionViewModelFactory (
    private val repository: DecryptionHistoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DecryptionViewModel::class.java)) {
            return DecryptionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}