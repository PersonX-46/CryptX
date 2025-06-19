package com.personx.cryptx.viewmodel.steganography

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SteganographyViewModelFactory(
    private val repository: SteganographyViewModelRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SteganographyViewModel::class.java)) {
            return SteganographyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}