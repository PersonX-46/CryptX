package com.personx.cryptx.screens.decryptscreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.util.TableInfo
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.Header
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

    Column {
        Header("DECRYPTION HISTORY", windowSizeClass)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                        )
                    )
                )
        ) {
            CyberpunkInputBox(
                modifier = Modifier.padding(10.dp),
                value = viewModel.searchQuery.value,
                onValueChange = { query: String ->
                    viewModel.updateSearchQuery(query)
                },
                placeholder = "Search history with name...",
            )
            HistoryScreen(
                history = viewModel.filteredHistory.value,
                onItemClick = {
                    viewModel.updateTitle(it.name)
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
                    viewModel.updateTitle(it.name)
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
    }
}