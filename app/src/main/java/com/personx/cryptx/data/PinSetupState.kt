package com.personx.cryptx.data

data class PinSetupState(
    val pin: String = "",
    val confirmPin: String = "",
    val step: Int = 1,
    val error: String? = null
)