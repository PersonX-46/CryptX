package com.personx.cryptx.screens.pinlogin

sealed class PinLoginEvent {
    data class EnterPin(val pin: String) : PinLoginEvent()
    data object Submit : PinLoginEvent()

}