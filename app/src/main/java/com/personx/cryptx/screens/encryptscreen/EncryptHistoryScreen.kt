package com.personx.cryptx.screens.encryptscreen

import android.widget.Toast
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.personx.cryptx.crypto.SessionKeyManager
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.screens.HistoryScreen
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel
import kotlinx.coroutines.launch

@Composable
fun EncryptHistoryScreen(
    viewModel: EncryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    HistoryScreen(
        history = viewModel.history.value,
        onEditClick = { it: EncryptionHistory ->
            viewModel.updateId(it.id)
            viewModel.updateTitle(it.name)
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
            if (!SessionKeyManager.isSessionActive()) {
                navController.navigate("encrypt_pin_handler") {
                    popUpTo("encrypt_history") { inclusive = true } // clears entire backstack
                    launchSingleTop = true
                }
            } else {
                scope.launch {
                    if (viewModel.itemToDelete != null) {
                        viewModel.itemToDelete?.let { item ->
                            viewModel.deleteEncryptionHistory( item)
                            viewModel.refreshHistory()
                            Toast.makeText(context, "History deleted!", Toast.LENGTH_SHORT).show()
                            viewModel.prepareItemToDelete(null)
                        }
                    }
                }
            }

        },
        onItemClick = { it: EncryptionHistory ->
            viewModel.updateTitle(it.name)
            viewModel.updateSelectedAlgorithm(it.algorithm)
            viewModel.updateSelectedMode(it.transformation)
            viewModel.updateSelectedKeySize(it.keySize)
            viewModel.updateKeyText(it.key)
            viewModel.updateIVText(it.iv?:"")
            viewModel.updateInputText(it.secretText)
            viewModel.updateBase64Enabled(it.isBase64)
            viewModel.updateOutputText(it.encryptedOutput)
            viewModel.updatePinPurpose("save")
            navController.navigate("encrypt") {
                popUpTo("encrypt_history") { inclusive = true } // clears entire backstack
                launchSingleTop = true
            }
        },
        windowSizeClass = windowSizeClass
    )
}