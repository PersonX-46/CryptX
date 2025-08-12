package com.personx.cryptx.data

data class PassphraseSetupState(
    val passphrase: String = "",
    val confirmPassphrase: String = "",
    val step: Int = 1,
    val error: String? = null,
    val isCompleted: Boolean = false,
    val isLoading: Boolean = false,
)