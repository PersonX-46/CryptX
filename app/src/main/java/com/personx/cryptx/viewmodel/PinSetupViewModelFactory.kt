package com.personx.cryptx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.personx.cryptx.crypto.PinCryptoManager

/**
 * PinLoginViewModelFactory is a factory class for creating instances of PinLoginViewModel.
 * It takes a PinCryptoManager as a parameter to provide the necessary cryptographic operations.
 */
class PinSetupViewModelFactory(
    private val pinCryptoManager: PinCryptoManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PinSetupViewModel::class.java)) {
            return PinSetupViewModel(pinCryptoManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
