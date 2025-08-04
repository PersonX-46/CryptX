package com.personx.cryptx.screens.decryptscreen

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.personx.cryptx.screens.HistoryScreen
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel

import android.util.Log

@Composable
fun EncryptedHistoryHandler(
    viewModel: DecryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    HistoryScreen(
        history = viewModel.encryptionHistory.value,
        enableEditing = false,
        enableDeleting = false,
        onItemClick = {
            Log.d("EncryptedHistory", "Selected Item:")
            Log.d("EncryptedHistory", "Algorithm: ${it.algorithm}")
            Log.d("EncryptedHistory", "Mode: ${it.transformation}")
            Log.d("EncryptedHistory", "Key: ${it.key}")
            Log.d("EncryptedHistory", "IV: ${it.iv ?: "null"}")
            Log.d("EncryptedHistory", "Base64: ${it.isBase64}")
            Log.d("EncryptedHistory", "Encrypted Output: ${it.encryptedOutput}")
            viewModel.updateTitle(it.name)
            viewModel.updateSelectedAlgorithm(it.algorithm)
            viewModel.updateSelectedMode(it.transformation)
            viewModel.updateKeyText(it.key)
            viewModel.updateIVText(it.iv ?: "")
            viewModel.updateInputText(it.encryptedOutput)
            viewModel.updateBase64Enabled(it.isBase64)
            viewModel.updateOutputText("")

            navController.navigate("decrypt") {
                popUpTo("decrypt_encrypted_history_handler") { inclusive = true }
                launchSingleTop = true
            }
        },
        onEditClick = {},
        onDeleteClick = {},
        windowSizeClass = windowSizeClass,
    )
}
