package com.personx.cryptx.data

data class HomeScreenState (
    val showPinDialog : Boolean = false,
    val currentPin: String? = null,
    val newPin: String? = null,
    val confirmPin: String? = null,
)