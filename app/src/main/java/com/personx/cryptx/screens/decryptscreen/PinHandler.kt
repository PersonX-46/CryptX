package com.personx.cryptx.screens.decryptscreen

import android.widget.Toast
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.screens.pinlogin.PinLoginScreen
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun DecryptPinHandler(
    viewModel: DecryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val context = LocalContext.current
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()
    PinLoginScreen(
        pinCryptoManager = PinCryptoManager(context),
        onLoginSuccess = { pin: String ->
            when (state.pinPurpose) {
                "save" -> {
                    scope.launch {
                        val success = viewModel.insertDecryptionHistory(
                            id = state.id,
                            pin,
                            state.selectedAlgorithm,
                            state.selectedMode,
                            state.keyText,
                            state.ivText,
                            state.inputText,
                            state.isBase64Enabled,
                            state.outputText
                        )
                        if (success) {
                            navController.navigate("decrypt")
                            viewModel.refreshHistory(pin)
                            delay(200)
                            viewModel.clearOutput()
                            Toast.makeText(
                                context,
                                "Decryption history saved!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to save decryption history",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                "history" -> {
                    viewModel.refreshHistory(pin)
                    navController.navigate("decrypt_history")
                }

                "encrypted_history" -> {
                    viewModel.getAllEncryptionHistory(pin)
                    navController.navigate("decrypt_encrypted_history_handler")
                }

                "update" -> {
                    scope.launch {
                        val itemToUpdate = viewModel.createDecryptionHistory(
                            id = state.id,
                            algorithm = state.selectedAlgorithm,
                            transformation = state.selectedMode,
                            key = state.keyText,
                            iv = state.ivText,
                            isBase64 = state.isBase64Enabled,
                            encryptedText = state.inputText,
                            decryptedOutput = state.outputText
                        )
                        viewModel.prepareItemToUpdate(itemToUpdate)
                        viewModel.itemToUpdate?.let { item ->
                            viewModel.updateDecryptionHistory(pin, item)
                            viewModel.refreshHistory(pin)
                            navController.navigate("decrypt_history")
                            Toast.makeText(context, "History updated!", Toast.LENGTH_SHORT)
                                .show()
                            viewModel.prepareItemToUpdate(null)
                        }
                    }
                }
                "delete" -> {
                    scope.launch {
                        if (viewModel.itemToDelete != null) {
                            viewModel.itemToDelete?.let { item ->
                                viewModel.deleteDecryptionHistory(pin, item)
                                viewModel.refreshHistory(pin)
                                navController.navigate("decrypt_history")
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
            }
        },
        windowSizeClass = windowSizeClass
    )
}