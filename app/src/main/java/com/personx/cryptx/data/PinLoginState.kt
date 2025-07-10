package com.personx.cryptx.data

data class PinLoginState(
    val enteredPin: String = "",
    val error: String? = null,
    val isSuccess : Boolean = false,
    val isLoading: Boolean = false
)