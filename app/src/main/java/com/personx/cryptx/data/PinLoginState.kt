package com.personx.cryptx.data

data class PassphraseLoginState(
    val passphrase: String = "",
    val error: String? = null,
    val isSuccess : Boolean = false,
    val isLoading: Boolean = false
)