package com.personx.cryptx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.screens.pinlogin.PassphraseLoginScreen

/**
 * PinLoginViewModelFactory is a factory class for creating instances of PinLoginViewModel.
 * It takes a PinCryptoManager as a parameter to provide the necessary cryptographic operations.
 */
class PassphraseLoginViewModelFactory(
    private val pinCryptoManager: PinCryptoManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PinLoginViewModel.PassphraseLoginViewModel::class.java)) {
            return PinLoginViewModel.PassphraseLoginViewModel(pinCryptoManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
