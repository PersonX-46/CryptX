package com.personx.cryptx.viewmodel

import androidx.lifecycle.ViewModel
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.data.PinLoginState
import com.personx.cryptx.screens.pinlogin.PinLoginEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/* * PinLoginViewModel is responsible for managing the state of the PIN login feature.
 * It handles events related to entering and submitting a PIN, and interacts with the PinCryptoManager
 * to verify the entered PIN.
 */
class PinLoginViewModel(
    private val pinCryptoManager: PinCryptoManager
): ViewModel() {

    private val _state = MutableStateFlow(PinLoginState())
    val state: StateFlow<PinLoginState> = _state

    /**
     * Resets the state of the PIN login to its initial values.
     * This is useful when navigating away from the PIN login screen or when a new login attempt is needed.
     */
    fun resetState() {
        _state.value = PinLoginState()
    }

    /**
     * Handles events related to PIN login.
     * @param event The event to handle, which can be entering a PIN or submitting it.
     */
    fun event(event: PinLoginEvent) {
        when (event) {
            is PinLoginEvent.EnterPin -> {
                val newPin = event.pin
                if (newPin.length <= 4 && newPin.all { it.isDigit() }) {
                    _state.value = _state.value.copy(enteredPin = newPin, error = null)
                }
            }
            is PinLoginEvent.Submit -> {
                val current = _state.value
                if (current.enteredPin.length == 4 && pinCryptoManager.loadSessionKeyIfPinValid(current.enteredPin) != null) {
                    if (pinCryptoManager.verifyPin(current.enteredPin)) {
                        _state.value = current.copy(error = null, isSuccess = true)
                    } else {
                        _state.value = current.copy(error = "Incorrect PIN", enteredPin = "", isSuccess = false)
                    }
                } else {
                    _state.value = current.copy(error = "Invalid PIN", enteredPin = "", isSuccess = false)
                }
            }
        }
    }

}