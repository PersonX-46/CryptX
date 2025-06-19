package com.personx.cryptx.viewmodel.decryption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/* * DecryptionViewModelFactory is a factory class for creating instances of DecryptionViewModel.
 * It takes a DecryptionHistoryRepository as a parameter to provide the necessary data source.
 */

class DecryptionViewModelFactory (
    private val repository: DecryptionHistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DecryptionViewModel::class.java)) {
            return DecryptionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}