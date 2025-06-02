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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.CyberpunkKeySection
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.DecryptionViewModel
import kotlinx.coroutines.launch

@Composable
fun DecryptionScreen(
    viewModel: DecryptionViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state.value
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val cyberpunkGreen = Color(0xFF00FFAA)
    val surfaceColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)

    LaunchedEffect(state.selectedAlgorithm) {
        viewModel.updateAlgorithmList(context)
    }

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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(0.03f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Decryption Settings",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = cyberpunkGreen.copy(alpha = 0.8f)
                    )
                )

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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(0.03f))
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(0.03f))
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
                                checkedThumbColor = cyberpunkGreen,
                                checkedTrackColor = cyberpunkGreen.copy(alpha = 0.5f)
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

            // Output Section
            AnimatedVisibility(
                visible = state.outputText.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(0.03f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Decrypted Output",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = cyberpunkGreen.copy(alpha = 0.8f)
                                )
                            )

                            IconButton(
                                onClick = {
                                    scope.launch {
                                        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Copied", state.outputText)))
                                    }
                                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = cyberpunkGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.outputText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDecryptionScreen() {
    CryptXTheme(darkTheme = true) {
        DecryptionScreen()
    }
}