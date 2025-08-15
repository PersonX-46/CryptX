package com.personx.cryptx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.crypto.VaultManager
import com.personx.cryptx.data.PassphraseSetupState
import com.personx.cryptx.screens.pinsetup.PassphraseSetupEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * PinSetupViewModel is responsible for managing the state of the PIN setup process.
 * It handles events related to entering and confirming a PIN, and interacts with the PinCryptoManager
 * to securely store the PIN once it is confirmed.
 */
class PassphraseSetupViewModel(
    private val repository: PassphraseSetupRepository,
    private val pinCryptoManager: PinCryptoManager // keep your existing manager for now
) : ViewModel() {

    private val _state = MutableStateFlow(PassphraseSetupState())
    val state: StateFlow<PassphraseSetupState> = _state

    fun event(event: PassphraseSetupEvent) {
        when (event) {
            is PassphraseSetupEvent.EnterPassphrase -> {
                // Allow any characters and unlimited length. You may optionally enforce a minimum length.
                _state.value = _state.value.copy(passphrase = event.value)
            }

            is PassphraseSetupEvent.EnterConfirmPassphrase -> {
                _state.value = _state.value.copy(confirmPassphrase = event.value)
            }

            is PassphraseSetupEvent.Continue -> {
                val current = _state.value
                // Move from step 1 -> step 2 if not blank
                if (current.step == 1) {
                    if (current.passphrase.isNotBlank()) {
                        _state.value = current.copy(step = 2, error = null)
                    } else {
                        _state.value = current.copy(error = "Passphrase cannot be empty")
                    }
                    return
                }

                // Step 2: confirm
                if (current.step == 2) {
                    if (current.passphrase == current.confirmPassphrase && current.passphrase.isNotBlank()) {
                        // Passphrase confirmed, store securely
                        _state.value = _state.value.copy(isLoading = true, error = null)
                        viewModelScope.launch(Dispatchers.IO) {
                            try {
                                // NOTE: consider changing setupPin to accept CharArray for extra security
                                pinCryptoManager.setupPin(current.passphrase)
                                repository.createVault()
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    isCompleted = true,
                                    error = null
                                )
                            } catch (e: Exception) {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    isCompleted = false,
                                    error = "Failed to store passphrase"
                                )
                            }
                        }
                    } else {
                        _state.value = _state.value.copy(
                            error = "Passphrases do not match",
                            passphrase = "",
                            confirmPassphrase = "",
                            step = 1
                        )
                    }
                }
            }
        }
    }
}
