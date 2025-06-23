package com.personx.cryptx.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.MutableStateFlow

data class HomeScreenState (
    val showPinDialog : Boolean = false,
    val currentPin: String? = null,
    val newPin: String? = null,
    val confirmPin: String? = null,
)