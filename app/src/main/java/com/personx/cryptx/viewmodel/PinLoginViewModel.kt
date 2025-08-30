package com.personx.cryptx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.data.PassphraseLoginState
import com.personx.cryptx.screens.pinlogin.PassphraseLoginEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* * PinLoginViewModel is responsible for managing the state of the PIN login feature.
 * It handles events related to entering and submitting a PIN, and interacts with the PinCryptoManager
 * to verify the entered PIN.
 */
class PinLoginViewModel(
    private val pinCryptoManager: PinCryptoManager
) : ViewModel() {

    private val _state = MutableStateFlow(PassphraseLoginState())
    val state: StateFlow<PassphraseLoginState> = _state

    fun resetState() {
        _state.value = PassphraseLoginState()
    }

    class PassphraseLoginViewModel(
        private val passphraseCryptoManager: PinCryptoManager
    ) : ViewModel() {

        private val _state = MutableStateFlow(PassphraseLoginState())
        val state: StateFlow<PassphraseLoginState> = _state

        fun resetState() {
            _state.value = PassphraseLoginState()
        }

        fun event(event: PassphraseLoginEvent) {
            when (event) {
                is PassphraseLoginEvent.EnterPassphrase -> {
                    val newPass = event.passphrase
                    // Example rule: min 6 chars, no restriction on digits only
                    if (newPass.length <= 128) { // can set max length
                        _state.value = _state.value.copy(passphrase = newPass, error = null)
                    }
                }
                is PassphraseLoginEvent.Submit -> {
                    val current = _state.value
                    if (current.passphrase.isNotEmpty()) {
                        _state.value = current.copy(isLoading = true)

                        viewModelScope.launch(Dispatchers.IO) {
                            val success = try {
                                passphraseCryptoManager.verifyPin(current.passphrase)
                            } catch (e: Exception) {
                                false
                            }

                            _state.value = if (success) {
                                current.copy(error = null, isSuccess = true, isLoading = false)
                            } else {
                                current.copy(
                                    error = "Incorrect passphrase",
                                    passphrase = "",
                                    isSuccess = false,
                                    isLoading = false
                                )
                            }
                        }
                    } else {
                        _state.value = current.copy(
                            error = "Passphrase must be at least 6 characters",
                            passphrase = "",
                            isSuccess = false
                        )
                    }
                }
            }
        }
    }
}
