package com.personx.cryptx.screens.decryptscreen

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.personx.cryptx.screens.HistoryScreen
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel

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
            viewModel.updateSelectedAlgorithm(it.algorithm)
            viewModel.updateSelectedMode(it.transformation)
            viewModel.updateKeyText(it.key)
            viewModel.updateIVText(it.iv ?: "")
            viewModel.updateInputText(it.encryptedOutput)
            viewModel.updateBase64Enabled(it.isBase64)
            viewModel.updateOutputText("")
            navController.navigate("decrypt") {
                popUpTo("decrypt_encrypted_history_handler") { inclusive = true } // clears entire backstack
                launchSingleTop = true
            }
        },
        onEditClick = {},
        onDeleteClick = {},
        windowSizeClass = windowSizeClass,
    )
}