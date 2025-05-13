package com.personx.cryptx.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.R
import com.example.cryptography.algorithms.SymmetricBasedAlgorithm
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.CyberpunkKeySection
import com.personx.cryptx.components.CyberpunkOutputSection
import com.personx.cryptx.components.Toast
import com.example.cryptography.data.CryptoParams
import com.personx.cryptx.ui.theme.CryptXTheme
import com.example.cryptography.utils.CryptoUtils.decodeBase64ToSecretKey
import com.example.cryptography.utils.CryptoUtils.decodeStringToByteArray
import com.example.cryptography.utils.CryptoUtils.encodeByteArrayToString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DecryptionScreen() {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    val algorithms = stringArrayResource(R.array.supported_algorithms_list).toList()
    val selectedAlgorithm = remember { mutableStateOf(algorithms.first()) }

    val transformationList = remember { mutableStateOf(emptyList<String>()) }
    val keySizeList = remember { mutableStateOf(emptyList<String>()) }

    val selectedMode = remember { mutableStateOf("") }
    val selectedKeySize = remember { mutableIntStateOf(128) }

    val inputText = remember { mutableStateOf("") }
    val outputText = remember { mutableStateOf("") }

    val ivText = remember { mutableStateOf("") }
    val keyText = remember { mutableStateOf("") }

    val enableIV = remember { mutableStateOf(false) }
    val isBase64Enabled = remember { mutableStateOf(false) }
    val showCopiedToast = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Re-generate key and mode list when algorithm changes
    LaunchedEffect(selectedAlgorithm.value) {
        transformationList.value = getTransformations(context, selectedAlgorithm.value)
        keySizeList.value = getKeySizes(context, selectedAlgorithm.value)
        selectedKeySize.intValue = keySizeList.value.firstOrNull()?.toIntOrNull() ?: 128
        selectedMode.value = transformationList.value.firstOrNull() ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "DECRYPTION",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF00FFAA)
            ),
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Algorithm Selection
        CyberpunkDropdown(
            items = algorithms,
            selectedItem = selectedAlgorithm.value,
            onItemSelected = { selectedAlgorithm.value = it },
            label = "SELECT ALGORITHM",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (selectedAlgorithm.value != "RSA") {
            // Mode Selection
            CyberpunkDropdown(
                items = transformationList.value,
                selectedItem = selectedMode.value,
                onItemSelected = {
                    selectedMode.value = it
                    enableIV.value = !it.contains("ECB")
                    if (!enableIV.value) ivText.value = ""
                },
                label = "SELECT MODE",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Input Section
            CyberpunkInputBox(
                value = inputText.value,
                onValueChange = { inputText.value = it },
                placeholder = "Enter ciphertext to decrypt...",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Key Section
            CyberpunkKeySection(
                keyText = keyText.value,
                onKeyTextChange = { keyText.value = it },
                onGenerateKey = {
                    try {
                        val newKey = SymmetricBasedAlgorithm().generateKey(
                            selectedAlgorithm.value,
                            selectedKeySize.intValue
                        )
                        keyText.value = encodeByteArrayToString(newKey.encoded).trim()
                    } catch (e: Exception) {
                        outputText.value = "Key generation failed: ${e.message}"
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                title = "KEY SECTION"
            )

            // IV Section (conditionally shown)
            if (enableIV.value) {
                CyberpunkInputBox(
                    value = ivText.value,
                    onValueChange = { ivText.value = it },
                    placeholder = "Enter IV used for encryption...",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Options Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Base64 Input",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Switch(
                    checked = isBase64Enabled.value,
                    onCheckedChange = { isBase64Enabled.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00FFAA),
                        checkedTrackColor = Color(0xFF00FFAA).copy(alpha = 0.5f)
                    )
                )
            }

            // Decrypt Button
            CyberpunkButton(
                onClick = {
                    try {
                        if (inputText.value.isBlank()) {
                            outputText.value = "Input text cannot be empty."
                            return@CyberpunkButton
                        }

                        val iv = try {
                            if (!enableIV.value || ivText.value.isBlank()) {
                                null // Don't generate random IV for decryption
                            } else {
                                decodeStringToByteArray(ivText.value)
                            }
                        } catch (e: Exception) {
                            outputText.value = "Invalid IV: ${e.message}"
                            return@CyberpunkButton
                        }

                        val key = try {
                            decodeBase64ToSecretKey(keyText.value, selectedAlgorithm.value)
                        } catch (e: Exception) {
                            outputText.value = "Invalid key: ${e.message}"
                            return@CyberpunkButton
                        }

                        val transformation = if (selectedAlgorithm.value == "ChaCha20")
                            "ChaCha20"
                        else
                            "${selectedAlgorithm.value}/${selectedMode.value}"

                        val params = CryptoParams(
                            data = inputText.value,
                            key = key,
                            transformation = transformation,
                            iv = iv,
                            useBase64 = isBase64Enabled.value
                        )

                        outputText.value = SymmetricBasedAlgorithm().decrypt(params)
                    } catch (e: Exception) {
                        outputText.value = "Decryption failed: ${e.message}"
                    }
                },
                icon = Icons.Default.LockOpen,
                text = "DECRYPT",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Output Section
            if (outputText.value.isNotEmpty()) {
                CyberpunkOutputSection(
                    output = outputText.value,
                    onCopy = {
                        scope.launch {
                            clipboard.setText(AnnotatedString(outputText.value))
                            showCopiedToast.value = true
                            delay(2000)
                            showCopiedToast.value = false
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    // Toast notification
    if (showCopiedToast.value) {
        Toast("Decrypted text copied to clipboard!")
    }
}

@Preview
@Composable
fun PreviewDecryptionScreen() {
    CryptXTheme(darkTheme = true) {
        DecryptionScreen()
    }
}