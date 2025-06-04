package com.personx.cryptx.screens

import android.content.ClipData
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.CyberpunkKeySection
import com.personx.cryptx.components.CyberpunkOutputSection
import com.personx.cryptx.components.Header
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.screens.pinlogin.PinLoginScreen
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.decryption.DecryptionHistoryRepository
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DecryptionScreen(
    repository: DecryptionHistoryRepository
) {
    val factory = remember { DecryptionViewModelFactory(repository) }
    val viewModel: DecryptionViewModel = viewModel(factory = factory)
    val context = LocalContext.current
    val state = viewModel.state.value
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val cyberpunkGreen = Color(0xFF00FFAA)

    LaunchedEffect(state.selectedAlgorithm) {
        viewModel.updateAlgorithmList(context)
    }

    when (state.currentScreen) {
        "main" -> {
            Column {
                Header("DECRYPTION")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                    MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                                )
                            )
                        )
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Algorithm Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(0.03f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Decryption Algorithm",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = cyberpunkGreen.copy(alpha = 0.8f)
                                    ),
                                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                                )
                                IconButton(
                                    onClick = {
                                        viewModel.updatePinPurpose("history")
                                        viewModel.updateCurrentScreen("pin_login")
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "History",
                                        tint = cyberpunkGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            CyberpunkDropdown(
                                items = stringArrayResource(R.array.supported_algorithms_list).toList(),
                                selectedItem = state.selectedAlgorithm,
                                onItemSelected = { viewModel.updateSelectedAlgorithm(it) },
                                label = "Algorithm"
                            )

                            if (state.selectedAlgorithm != "RSA") {
                                Spacer(modifier = Modifier.height(8.dp))

                                CyberpunkDropdown(
                                    items = state.transformationList,
                                    selectedItem = state.selectedMode,
                                    onItemSelected = { viewModel.updateSelectedMode(it) },
                                    label = "Cipher Mode"
                                )
                            }
                        }
                    }

                    if (state.selectedAlgorithm != "RSA") {
                        // Input Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onSurface.copy(0.03f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Encrypted Input",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = cyberpunkGreen.copy(alpha = 0.8f)
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                CyberpunkInputBox(
                                    value = state.inputText,
                                    onValueChange = { viewModel.updateInputText(it) },
                                    placeholder = "Paste ciphertext here...",
                                    modifier = Modifier.height(120.dp)
                                )
                            }
                        }

                        // Security Parameters Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onSurface.copy(0.03f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Decryption Keys",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = cyberpunkGreen.copy(alpha = 0.8f)
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                CyberpunkKeySection(
                                    keyText = state.keyText,
                                    onKeyTextChange = { viewModel.updateKeyText(it) },
                                    onGenerateKey = { viewModel.generateKey() },
                                    title = "Decryption Key"
                                )

                                if (state.enableIV) {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    CyberpunkInputBox(
                                        value = state.ivText,
                                        onValueChange = { viewModel.updateIVText(it) },
                                        placeholder = "Enter Initialization Vector (IV)...",
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Base64 Encoded Input",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Switch(
                                        checked = state.isBase64Enabled,
                                        onCheckedChange = { viewModel.updateBase64Enabled(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedBorderColor = cyberpunkGreen,
                                            checkedThumbColor = cyberpunkGreen,
                                            checkedTrackColor = Color.Transparent,
                                            uncheckedTrackColor = Color.Transparent,
                                            uncheckedThumbColor = cyberpunkGreen,
                                            uncheckedBorderColor = cyberpunkGreen
                                        )
                                    )
                                }
                            }
                        }

                        // Action Button
                        CyberpunkButton(
                            onClick = { viewModel.decrypt(context) },
                            icon = Icons.Default.LockOpen,
                            text = "DECRYPT",
                            modifier = Modifier.fillMaxWidth(),
                        )


                        if (state.outputText.isNotEmpty()) {
                            AnimatedVisibility(
                                visible = state.outputText.isNotEmpty(),
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.onSurface
                                            .copy(0.05f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Decrypted Output",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = cyberpunkGreen.copy(alpha = 0.8f)
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        CyberpunkOutputSection(
                                            output = state.outputText,
                                            onCopy = {
                                                scope.launch {
                                                    clipboard.setClipEntry(
                                                        ClipEntry(
                                                            ClipData.newPlainText(
                                                                "Copied",
                                                                state.outputText
                                                            )
                                                        )
                                                    )

                                                }
                                                Toast.makeText(
                                                    context,
                                                    "Copied!",
                                                    Toast.LENGTH_SHORT)
                                                    .show()
                                            },
                                            onSave = {
                                                viewModel.updatePinPurpose("save")
                                                viewModel.updateCurrentScreen("pin_login")
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            }


        "pin_login" -> {
            // Pin Login Screen
            PinLoginScreen(
                pinCryptoManager = PinCryptoManager(context),
                onLoginSuccess = { pin: String ->
                    when (state.pinPurpose) {
                        "save" -> {
                            scope.launch {
                                val success = viewModel.insertDecryptionHistory(
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
                                    viewModel.updateCurrentScreen("main")
                                    viewModel.refreshHistory(pin)
                                    delay(200)
                                    viewModel.clearOutput()
                                    Toast.makeText(context, "Decryption history saved!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save decryption history", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        "history" -> {
                            viewModel.refreshHistory(pin)
                            viewModel.updateCurrentScreen("history_screen")
                        }
                    }

                }
            )
        }
        "history_screen" -> {
            // History Screen
            HistoryScreen(
                history = viewModel.history.value
            ) {
                viewModel.updateSelectedAlgorithm(it.algorithm)
                viewModel.updateSelectedMode(it.transformation)
                viewModel.updateKeyText(it.key)
                viewModel.updateIVText(it.iv?:"")
                viewModel.updateInputText(it.encryptedText)
                viewModel.updateBase64Enabled(it.isBase64)
                viewModel.updateOutputText(it.decryptedOutput)
                viewModel.updateCurrentScreen("main")
            }
        }
    }
}

@Preview
@Composable
fun PreviewDecryptionScreen() {
    CryptXTheme(darkTheme = true) {
        DecryptionScreen(DecryptionHistoryRepository(LocalContext.current))
    }
}