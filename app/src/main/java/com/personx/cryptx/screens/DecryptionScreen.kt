package com.personx.cryptx.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import com.personx.cryptx.R
import com.personx.cryptx.algorithms.SymmetricBasedAlgorithm
import com.personx.cryptx.components.CryptographicTextBox
import com.personx.cryptx.components.MaterialDropdownMenu
import com.personx.cryptx.data.CryptoParams
import com.personx.cryptx.utils.CryptoUtils.decodeBase64ToSecretKey
import com.personx.cryptx.utils.CryptoUtils.decodeStringToByteArray
import com.personx.cryptx.utils.CryptoUtils.encodeByteArrayToString
import com.personx.cryptx.utils.CryptoUtils.generateSecretKey

@Composable
fun DecryptionScreen() {
    val context = LocalContext.current
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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MaterialDropdownMenu(
            items = algorithms,
            onItemSelected = { selectedAlgorithm.value = it },
            label = "Algorithms",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (selectedAlgorithm.value != "RSA") {
            CryptographicTextBox(
                transformationList = transformationList.value,
                onTransformationSelected = {
                    selectedMode.value = it
                    enableIV.value = !it.contains("ECB")
                    if (!enableIV.value) ivText.value = ""
                },
                keyList = keySizeList.value,
                onKeySelected = { selectedKeySize.intValue = it.toInt() },
                placeholder1 = "Enter Text to Decrypt",
                placeholder2 = "The Decrypted Text Will Appear Here",
                enableTextInput = true,
                text1 = inputText.value,
                text2 = outputText.value,
                onText1Change = { inputText.value = it },
                checkSwitch = isBase64Enabled.value,
                onSwitchChange = { isBase64Enabled.value = it },
                enableIV = enableIV.value,
                ivText = ivText.value,
                onIvTextChange = { ivText.value = it },
                keyText = keyText.value,
                onKeyTextChange = { keyText.value = it },
                onKeyGenerateClicked = {
                    try {
                        val newKey = generateSecretKey(selectedAlgorithm.value, selectedKeySize.intValue)
                        keyText.value = encodeByteArrayToString(newKey.encoded).trim()
                    } catch (e: Exception) {
                        outputText.value = "Key generation failed: ${e.message}"
                    }
                },
                onText2Change = { outputText.value = it },
                onSubmit = {
                    try {
                        if (inputText.value.isBlank()) {
                            outputText.value = "Input text cannot be empty."
                            return@CryptographicTextBox
                        }

                        // Get IV from user input
                        val iv = try {
                            if (!enableIV.value || ivText.value.isBlank()) {
                                null // Do NOT generate random IV for decryption
                            } else {
                                decodeStringToByteArray(ivText.value)
                            }
                        } catch (e: Exception) {
                            outputText.value = "Invalid IV: ${e.message}"
                            return@CryptographicTextBox
                        }

                        // Get key from user input
                        val key = try {
                            decodeBase64ToSecretKey(keyText.value, selectedAlgorithm.value)
                        } catch (e: Exception) {
                            outputText.value = "Invalid key: ${e.message}"
                            return@CryptographicTextBox
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
            )
        }
    }
}