package com.personx.cryptx.screens.pinlogin

sealed class PassphraseLoginEvent {
    data class EnterPassphrase(val passphrase: String) : PassphraseLoginEvent()
    data object Submit : PassphraseLoginEvent()
}
