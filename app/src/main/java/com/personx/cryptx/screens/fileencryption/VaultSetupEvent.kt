package com.personx.cryptx.screens.fileencryption

sealed class VaultSetupEvent {
    data class EnterPassphrase(val passphrase: String) : VaultSetupEvent()
    object Submit : VaultSetupEvent()
}