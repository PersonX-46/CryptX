package com.personx.cryptx.data

import kotlin.io.encoding.Base64

data class SettingsScreenState (
    val showPinDialog : Boolean = false,
    val showImportDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showBase64: Boolean = false,
    val currentPin: String? = null,
    val newPin: String? = null,
    val confirmPin: String? = null,
    val backupResult: String? = null,
)