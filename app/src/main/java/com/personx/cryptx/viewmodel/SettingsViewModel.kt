package com.personx.cryptx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.data.SettingsScreenState
import com.personx.cryptx.database.encryption.DatabaseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val pinCryptoManager: PinCryptoManager
): ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state

    fun resetState() {
        _state.value = SettingsScreenState()
    }

    fun updateShowPinDialog(show: Boolean) {
        _state.value = _state.value.copy(showPinDialog = show)
    }

    fun updateCurrentPin(pin: String?) {
        _state.value = _state.value.copy(currentPin = pin)
    }

    fun updateNewPin(pin: String?) {
        _state.value = _state.value.copy(newPin = pin)
    }

    fun updateConfirmPin(pin: String?) {
        _state.value = _state.value.copy(confirmPin = pin)
    }

    fun updatePin(
        oldPin: String,
        newPin: String,
        confirmPin: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success =
                if (newPin == confirmPin && newPin.length == 4 && newPin.all { it.isDigit() }) {
                    val result = pinCryptoManager.changePinAndRekeyDatabase(newPin)

                    if (result) {
                        // clear old database instance after rekeying
                        DatabaseProvider.clearDatabaseInstance()
                    }

                    _state.value = _state.value.copy(
                        showPinDialog = false,
                        currentPin = null,
                        newPin = null,
                        confirmPin = null
                    )
                    result
                } else {
                    _state.value = _state.value.copy(
                        showPinDialog = true,
                        currentPin = oldPin,
                        newPin = newPin,
                        confirmPin = confirmPin
                    )
                    false
                }

            onResult(success)
        }
    }
}