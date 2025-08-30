package com.personx.cryptx.viewmodel.fileencryption

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VaultViewModelFactory(
    private val repository: VaultRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VaultViewModel::class.java)) {
            return VaultViewModel(
                repository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}