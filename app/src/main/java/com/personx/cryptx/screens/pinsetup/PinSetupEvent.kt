package com.personx.cryptx.screens.pinsetup

sealed class PassphraseSetupEvent {
    data class EnterPassphrase(val value: String) : PassphraseSetupEvent()
    data class EnterConfirmPassphrase(val value: String) : PassphraseSetupEvent()
    object Continue : PassphraseSetupEvent()
}

