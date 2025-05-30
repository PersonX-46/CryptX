package com.personx.cryptx.screens.pinsetup

sealed class PinSetupEvent{

    data class EnterPin(val value: String) : PinSetupEvent()
    data class EnterConfirmPin(val value: String) : PinSetupEvent()
    data object Continue : PinSetupEvent()
}
