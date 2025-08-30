package com.personx.cryptx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.personx.cryptx.crypto.PinCryptoManager

/**
 * PinLoginViewModelFactory is a factory class for creating instances of PinLoginViewModel.
 * It takes a PinCryptoManager as a parameter to provide the necessary cryptographic operations.
 */
class PinSetupViewModelFactory(
    private val repository: PassphraseSetupRepository,
    private val pinCryptoManager: PinCryptoManager,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PassphraseSetupViewModel::class.java)) {
            return PassphraseSetupViewModel(pinCryptoManager = pinCryptoManager, repository = repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
