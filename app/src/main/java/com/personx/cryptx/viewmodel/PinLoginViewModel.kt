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

    fun event(event: PinLoginEvent) {

        when (event) {
            is PinLoginEvent.EnterPin -> {
                if (event.pin.length <= 4 && event.pin.all { it.isDigit() }) {
                    _state.value = _state.value.copy(enteredPin = event.pin)
                }
            }
            is PinLoginEvent.Submit -> {
                val current = _state.value
                if (pinCryptoManager.verifyPin(current.enteredPin)) {
                    Log.d("IS CORRECT PIN", "CORRECT")
                    _state.value = current.copy(error = null, isSuccess = true)
                } else {
                    _state.value = current.copy(error = "Incorrect PIN", enteredPin = "", isSuccess = false)
                    Log.d("IS CORRECT PIN", "WRONG")
                }
            }
        }
    }
}