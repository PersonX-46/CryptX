package com.personx.cryptx.screens.decryptscreen

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.personx.cryptx.screens.HistoryScreen
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel

@Composable
fun DecryptHistoryScreen(
    viewModel: DecryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    HistoryScreen(
        history = viewModel.history.value,
        onItemClick = {
            viewModel.updateSelectedAlgorithm(it.algorithm)
            viewModel.updateSelectedMode(it.transformation)
            viewModel.updateKeyText(it.key)
            viewModel.updateIVText(it.iv?:"")
            viewModel.updateInputText(it.encryptedText)
            viewModel.updateBase64Enabled(it.isBase64)
            viewModel.updateOutputText(it.decryptedOutput)
            navController.navigate("decrypt")
        },
        onEditClick = {
            viewModel.updateId(it.id)
            viewModel.updateSelectedAlgorithm(it.algorithm)
            viewModel.updateSelectedMode(it.transformation)
            viewModel.updateKeyText(it.key)
            viewModel.updateIVText(it.iv?:"")
            viewModel.updateInputText(it.encryptedText)
            viewModel.updateBase64Enabled(it.isBase64)
            viewModel.updateOutputText(it.decryptedOutput)
            viewModel.updatePinPurpose("update")
            navController.navigate("decrypt")
        },
        onDeleteClick = { item ->
            viewModel.prepareItemToDelete(item)
            viewModel.updatePinPurpose("delete")
            navController.navigate("decrypt_pin_handler")
        },
        windowSizeClass = windowSizeClass,
        chooseFromEncrypt = {
            viewModel.updatePinPurpose("encrypted_history")
            navController.navigate("decrypt_pin_handler")
        }
    )
}