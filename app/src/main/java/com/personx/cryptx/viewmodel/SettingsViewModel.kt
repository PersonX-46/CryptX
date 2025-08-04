package com.personx.cryptx.viewmodel

import android.app.Application
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personx.cryptx.AppFileManager
import com.personx.cryptx.backup.BackupManager
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.data.SettingsScreenState
import com.personx.cryptx.database.encryption.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class SettingsViewModel(
    private val pinCryptoManager: PinCryptoManager,
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state

    private var tempExportPassword: String? = null
    private var pendingExport = false

    fun resetState() {
        _state.value = SettingsScreenState()
        tempExportPassword = null
        pendingExport = false
    }

    fun updateShowBase64(show: Boolean) {
        _state.value = _state.value.copy(showBase64 = show)
    }

    fun updateShowPinDialog(show: Boolean) {
        _state.value = _state.value.copy(showPinDialog = show)
    }

    fun updateCurrentPin(pin: String?) {
        _state.value = _state.value.copy(currentPin = pin)
    }

    fun updateNewPin(pin: String?) {
        _state.value = _state.value.copy(newPin = pin)
    }

    fun updateShowHidePlainTextDialog(hide: Boolean) {
        _state.value = _state.value.copy(hideShowPlainText = hide)
    }

    fun updateConfirmPin(pin: String?) {
        _state.value = _state.value.copy(confirmPin = pin)
    }

    fun updateBackupResult(result: String?) {
        _state.value = _state.value.copy(backupResult = result)
    }

    fun updateShowImportDialog(show: Boolean) {
        _state.value = _state.value.copy(showImportDialog = show)
    }

    fun updateShowExportDialog(show: Boolean) {
        _state.value = _state.value.copy(showExportDialog = show)
    }

    fun setExportPassword(pwd: String) {
        tempExportPassword = pwd
    }

    fun updatePin(
        oldPin: String,
        newPin: String,
        confirmPin: String,
        onResult: (Boolean) -> Unit
    ) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success =
                    if (newPin == confirmPin && newPin.length == 6 && newPin.all { it.isDigit() }) {
                        val result = pinCryptoManager.changePinAndRekeyDatabase(newPin)
                        if (result) DatabaseProvider.clearDatabaseInstance()
                        _state.value = _state.value.copy(
                            showPinDialog = false,
                            currentPin = null,
                            newPin = null,
                            confirmPin = null
                        )
                        result
                    } else {
                        _state.value = _state.value.copy(
                            showPinDialog = true,
                            currentPin = oldPin,
                            newPin = newPin,
                            confirmPin = confirmPin
                        )
                        false
                    }
                viewModelScope.launch(Dispatchers.Main) {
                    onResult(success)
                }
            } finally {
                resetState()
            }
        }
    }

    private fun saveZipToDownloads(zipFile: File, fileName: String): Boolean {
        return try {
            val (file, uri) = AppFileManager.saveToPublicDirectory(
                context = application,
                subPath = "cryptx/backups",
                filename = fileName,
                content = zipFile.readBytes(),
                mimeType = "application/zip"
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun launchExportAfterPassword(password: String) {
        tempExportPassword = password
        pendingExport = true
        exportBackupToDownloads()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun exportBackupToDownloads() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val password = tempExportPassword
                if (password.isNullOrEmpty()) {
                    Log.e("SettingsViewModel", "❌ Cannot export: password is null or empty")
                    updateBackupResult("❌ Password is missing for backup.")
                    return@launch
                }

                Log.d("SettingsViewModel", "📦 Exporting backup using password")
                val backupFile = BackupManager.exportBackup(application, password)

                if (backupFile != null && backupFile.exists()) {
                    Log.d("SettingsViewModel", "Backup file created: ${backupFile.absolutePath}")
                    val success = saveZipToDownloads(backupFile, "cryptx.backupx")
                    if (success) {
                        Log.d("SettingsViewModel", "Backup successfully saved to Downloads")
                        updateBackupResult("Backup exported to Downloads/cryptx/")
                    } else {
                        Log.e("SettingsViewModel", "❌ Failed to write backup to Downloads")
                        updateBackupResult("❌ Failed to write backup to Downloads.")
                    }
                } else {
                    Log.e("SettingsViewModel", "❌ Backup file not created")
                    updateBackupResult("❌ Failed to create backup file.")
                }

            } catch (e: Exception) {
                Log.e("SettingsViewModel", "❌ Exception during export: ${e.message}", e)
                updateBackupResult("❌ Error exporting backup: ${e.message}")
            } finally {
                Log.d("SettingsViewModel", "Clearing temporary export state")
                tempExportPassword = null
                pendingExport = false
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun importBackupFromUri(
        uri: Uri,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = try {
                Log.d("SettingsViewModel", "Importing backup from URI: $uri")
                val tempBackupFile = File(application.cacheDir, "import.backupx")
                application.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(tempBackupFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val success = BackupManager.importBackup(application, tempBackupFile, password)
                val message = if (success) {
                    Log.d("SettingsViewModel", "Backup restored successfully")
                    "Backup restored successfully!"
                } else {
                    Log.e("SettingsViewModel", "❌ Failed to restore backup")
                    "❌ Failed to restore backup."
                }

                updateBackupResult(message)
                tempBackupFile.delete()
                success
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "❌ Error importing backup: ${e.message}", e)
                updateBackupResult("❌ Error importing backup: ${e.message}")
                false
            } finally {
                resetState()
            }

            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}
