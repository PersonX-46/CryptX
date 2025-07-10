package com.personx.cryptx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.data.PinSetupState
import com.personx.cryptx.screens.pinsetup.PinSetupEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * PinSetupViewModel is responsible for managing the state of the PIN setup process.
 * It handles events related to entering and confirming a PIN, and interacts with the PinCryptoManager
 * to securely store the PIN once it is confirmed.
 */
class PinSetupViewModel(
    private val pinCryptoManager: PinCryptoManager
) : ViewModel() {

    private val _state = MutableStateFlow(PinSetupState())
    val state: StateFlow<PinSetupState> = _state

    fun event(event: PinSetupEvent){
        when (event) {
            is PinSetupEvent.EnterPin -> {
                if (event.value.length <= 4 && event.value.all { it.isDigit() }) {
                    _state.value = _state.value.copy(pin = event.value)
                }
            }
            is PinSetupEvent.EnterConfirmPin -> {
                if (event.value.length <= 4 && event.value.all { it.isDigit() }) {
                    _state.value = _state.value.copy(confirmPin = event.value)
                }
            }
            is PinSetupEvent.Continue -> {
                val current = _state.value
                if ( current.step == 1 ) {
                    if (current.pin.length == 4) {
                        _state.value = current.copy(step = 2, error = null)
                    } else {
                        _state.value = current.copy(error = "Pin must be 4 digits")
                    }
                } else {
                    if ( current.pin == current.confirmPin) {
                        // Pin confirmed, can now securely store it
                        _state.value = _state.value.copy(isLoading = true)
                        viewModelScope.launch(Dispatchers.IO) {
                            try {
                                pinCryptoManager.setupPin(current.pin) // ✅ Store encrypted PIN data
                                _state.value = current.copy(error = null, isCompleted = true, isLoading = false)
                            } catch (e: Exception) {
                                _state.value = current.copy(error = "Failed to store PIN", pin = "", confirmPin = "", isCompleted = false, isLoading = false)
                            }
                        }
                    } else {
                        _state.value = current.copy(error = "Pins do not match", pin = "", confirmPin = "", isCompleted = false)
                    }
                }
            }
        }
    }
}