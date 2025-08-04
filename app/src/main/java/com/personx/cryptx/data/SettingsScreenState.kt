package com.personx.cryptx.data

import android.hardware.biometrics.PromptContentItemPlainText

data class SettingsScreenState (
    val showPinDialog : Boolean = false,
    val showImportDialog: Boolean = false,
    val showExportDialog: Boolean = false,
    val showBase64: Boolean = false,
    val hideShowPlainText: Boolean = false,
    val currentPin: String? = null,
    val newPin: String? = null,
    val confirmPin: String? = null,
    val backupResult: String? = null,
    val isLoading: Boolean = false
)