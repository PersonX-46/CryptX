package com.personx.cryptx.screens.decryptscreen

import android.widget.Toast
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.personx.cryptx.crypto.SessionKeyManager
import com.personx.cryptx.screens.HistoryScreen
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel
import kotlinx.coroutines.launch

@Composable
fun DecryptHistoryScreen(
    viewModel: DecryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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
            viewModel.updatePinPurpose("save")
            navController.navigate("decrypt") {
                popUpTo("decrypt_history") { inclusive = true } // clears entire backstack
                launchSingleTop = true
            }
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
            navController.navigate("decrypt") {
                popUpTo("decrypt_history") { inclusive = true } // clears entire backstack
                launchSingleTop = true
            }
        },
        onDeleteClick = { item ->
            viewModel.prepareItemToDelete(item)
            viewModel.updatePinPurpose("delete")
            if (!SessionKeyManager.isSessionActive()) {
                navController.navigate("decrypt_pin_handler"){
                    popUpTo("decrypt_history") { inclusive = true } // clears entire backstack
                    launchSingleTop = true
                }
            } else {
                scope.launch {
                    if (viewModel.itemToDelete != null) {
                        viewModel.itemToDelete?.let { item ->
                            viewModel.deleteDecryptionHistory(item)
                            viewModel.refreshHistory()
                            navController.navigate("decrypt_history") {
                                popUpTo("decrypt_pin_handler") { inclusive = true } // clears entire backstack
                                launchSingleTop = true
                            }
                            Toast.makeText(
                                context,
                                "History deleted!",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.prepareItemToDelete(null)
                        }
                    }
                }
            }
        },
        windowSizeClass = windowSizeClass,
        chooseFromEncrypt = {
            viewModel.updatePinPurpose("encrypted_history")
            if (!SessionKeyManager.isSessionActive()) {
                navController.navigate("decrypt_pin_handler") {
                    popUpTo("decrypt_history") { inclusive = true } // clears entire backstack
                    launchSingleTop = true
                }
            } else{
                viewModel.getAllEncryptionHistory()
                navController.navigate("decrypt_encrypted_history_handler") {
                    popUpTo("decrypt_pin_handler") { inclusive = true } // clears entire backstack
                    launchSingleTop = true
                }
            }
            }

    )
}