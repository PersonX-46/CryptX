package com.personx.cryptx.screens.encryptscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.screens.pinlogin.PinLoginScreen
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EncryptPinHandler(
    viewModel: EncryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController,
){
    val state = viewModel.state.value
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Log.d("EncryptPinHandlerPinPurpose", state.pinPurpose)
    PinLoginScreen(
        pinCryptoManager = PinCryptoManager(LocalContext.current),
        onLoginSuccess = { pin: String ->
            Log.d("EncryptPinHandler", state.pinPurpose)
            when (state.pinPurpose) {

                "save" -> {
                    Log.d("EncryptPinHandlerSave",state.pinPurpose)
                    Log.d("EncryptPinHandler", "Saving encryption history")
                    scope.launch {
                        try {
                            val success = viewModel.insertEncryptionHistory(
                                id = state.id,
                                pin = pin,
                                algorithm = state.selectedAlgorithm,
                                transformation = state.selectedMode,
                                keySize = state.selectedKeySize,
                                key = state.keyText,
                                iv = if (state.enableIV) state.ivText else null,
                                secretText = state.inputText,
                                isBase64 = state.isBase64Enabled,
                                encryptedOutput = state.outputText
                            )
                            if (success) {
                                viewModel.refreshHistory(pin)
                                delay(200)
                                viewModel.clearOutput()
                                Toast.makeText(context, "Encryption history saved!", Toast.LENGTH_SHORT).show()
                                navController.navigate("encrypt") {
                                    popUpTo("encrypt_pin_handler") { inclusive = true } // clears entire backstack
                                    launchSingleTop = true
                                }

                            } else {
                                Toast.makeText(context, "Failed to save history.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error saving history: ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                "history" -> {
                    Log.d("EncryptPinHandler", state.pinPurpose)
                    scope.launch {
                        Log.d("EncryptPinHandler", "Refreshing history")
                        viewModel.refreshHistory(pin)
                        Log.d("EncryptPinHandler", "Navigating to history")
                        navController.navigate("encrypt_history") {
                            popUpTo("encrypt_pin_handler") { inclusive = true } // clears entire backstack
                            launchSingleTop = true
                        }
                        Log.d("EncryptPinHandler", "History refreshed and navigation complete")
                    }
                }

                "delete" -> {
                    scope.launch {
                        if (viewModel.itemToDelete != null) {
                            viewModel.itemToDelete?.let { item ->
                                viewModel.deleteEncryptionHistory(pin, item)
                                viewModel.refreshHistory(pin)
                                navController.navigate("encrypt_history") {
                                    popUpTo("encrypt_pin_handler") { inclusive = true } // clears entire backstack
                                    launchSingleTop = true
                                }
                                Toast.makeText(context, "History deleted!", Toast.LENGTH_SHORT).show()
                                viewModel.prepareItemToDelete(null)
                            }
                        }
                    }
                }

                "update" -> {
                    scope.launch {
                        val itemToUpdate = viewModel.createEncryptedHistory(
                            id = state.id,
                            algorithm = state.selectedAlgorithm,
                            transformation = state.selectedMode,
                            keySize = state.selectedKeySize,
                            key = state.keyText,
                            iv = state.ivText,
                            secretText = state.inputText,
                            isBase64 = state.isBase64Enabled,
                            encryptedOutput = state.outputText
                        )
                        viewModel.prepareItemToUpdate(itemToUpdate)
                        viewModel.itemToUpdate?.let { item ->
                            viewModel.updateEncryptionHistory(pin, item)
                            viewModel.refreshHistory(pin)
                            navController.navigate("encrypt_history") {
                                popUpTo("encrypt_pin_handler") { inclusive = true } // clears entire backstack
                                launchSingleTop = true
                            }
                            Toast.makeText(context, "History updated!", Toast.LENGTH_SHORT).show()
                            viewModel.prepareItemToUpdate(null)
                        }
                    }
                }
            }
        },
        windowSizeClass = windowSizeClass
    )
}
