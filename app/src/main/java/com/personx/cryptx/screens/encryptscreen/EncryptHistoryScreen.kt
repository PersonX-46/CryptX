package com.personx.cryptx.screens.encryptscreen

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.screens.HistoryScreen
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel

@Composable
fun EncryptHistoryScreen(
    viewModel: EncryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    HistoryScreen(
        history = viewModel.history.value,
        onEditClick = { it: EncryptionHistory ->
            viewModel.updateId(it.id)
            viewModel.updateSelectedAlgorithm(it.algorithm)
            viewModel.updateSelectedMode(it.transformation)
            viewModel.updateSelectedKeySize(it.keySize)
            viewModel.updateKeyText(it.key)
            viewModel.updateIVText(it.iv?:"")
            viewModel.updateInputText(it.secretText)
            viewModel.updateBase64Enabled(it.isBase64)
            viewModel.updateOutputText(it.encryptedOutput)
            viewModel.updatePinPurpose("update")
            navController.navigate("encrypt") {
                popUpTo("encrypt_history") { inclusive = true } // clears entire backstack
                launchSingleTop = true
            }
        },
        onDeleteClick = { it: EncryptionHistory ->
            viewModel.prepareItemToDelete(it)
            viewModel.updatePinPurpose("delete")
            navController.navigate("encrypt_pin_handler") {
                popUpTo("encrypt_history") { inclusive = true } // clears entire backstack
                launchSingleTop = true
            }
        },
        onItemClick = { it: EncryptionHistory ->
            viewModel.updateSelectedAlgorithm(it.algorithm)
            viewModel.updateSelectedMode(it.transformation)
            viewModel.updateSelectedKeySize(it.keySize)
            viewModel.updateKeyText(it.key)
            viewModel.updateIVText(it.iv?:"")
            viewModel.updateInputText(it.secretText)
            viewModel.updateBase64Enabled(it.isBase64)
            viewModel.updateOutputText(it.encryptedOutput)
            navController.navigate("encrypt") {
                popUpTo("encrypt_history") { inclusive = true } // clears entire backstack
                launchSingleTop = true
            }
        },
        windowSizeClass = windowSizeClass
    )
}