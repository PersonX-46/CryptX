package com.personx.cryptx.viewmodel.steganography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel

class SteganographyViewModelFactory(
    private val repository: SteganographyViewModelRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EncryptionViewModel::class.java)) {
            return SteganographyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}