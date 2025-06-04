package com.personx.cryptx.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.data.PinLoginState
import com.personx.cryptx.screens.pinlogin.PinLoginEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PinLoginViewModel(
    private val pinCryptoManager: PinCryptoManager
): ViewModel() {

    private val _state = MutableStateFlow(PinLoginState())
    val state: StateFlow<PinLoginState> = _state

    fun resetState() {
        _state.value = PinLoginState()
    }

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
                if (current.enteredPin.length == 4 && pinCryptoManager.getRawKeyIfPinValid(current.enteredPin) != null) {
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