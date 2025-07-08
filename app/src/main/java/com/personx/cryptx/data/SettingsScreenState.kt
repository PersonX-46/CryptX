package com.personx.cryptx.data

data class SettingsScreenState (
    val showPinDialog : Boolean = false,
    val showImportDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val currentPin: String? = null,
    val newPin: String? = null,
    val confirmPin: String? = null,
    val backupResult: String? = null,
)