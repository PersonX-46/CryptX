package com.personx.cryptx.screens


import android.content.Context
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.R
import com.personx.cryptx.algorithms.SymmetricBasedAlgorithm
import com.personx.cryptx.components.CryptographicTextBox
import com.personx.cryptx.components.MaterialDropdownMenu
import com.personx.cryptx.data.CryptoParams
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.utils.CryptoUtils.decodeStringToByteArray
import com.personx.cryptx.utils.CryptoUtils.decodeBase64ToSecretKey
import com.personx.cryptx.utils.CryptoUtils.encodeByteArrayToString
import com.personx.cryptx.utils.CryptoUtils.generateRandomIV
import com.personx.cryptx.utils.CryptoUtils.generateSecretKey
import com.personx.cryptx.utils.CryptoUtils.padTextToBlockSize
import java.security.SecureRandom

@Composable
fun MostUsedAlgo() {
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
        keyText.value = encodeByteArrayToString(
            generateSecretKey(selectedAlgorithm.value, selectedKeySize.intValue).encoded
        ).trim()
        ivText.value = encodeByteArrayToString(generateRandomIV(16))
    }

    LaunchedEffect(selectedMode.value) {
        if (!selectedMode.value.contains("ECB")) {
            keyText.value = encodeByteArrayToString(
                generateSecretKey(selectedAlgorithm.value, selectedKeySize.intValue).encoded
            ).trim()
            ivText.value = encodeByteArrayToString(generateRandomIV(16))
        } else {
            keyText.value = encodeByteArrayToString(
                generateSecretKey(selectedAlgorithm.value, selectedKeySize.intValue).encoded
            ).trim()
            enableIV.value = false
            ivText.value = ""
        }

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
                placeholder1 = "Enter Text to Encrypt",
                placeholder2 = "The Encrypted Text Will Appear Here",
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

                        val iv = try {
                            if (!enableIV.value || ivText.value.isBlank()) {
                                // Generate a random IV of 16 bytes (for AES, Blowfish)
                                SecureRandom().generateSeed(16)
                            } else {
                                decodeStringToByteArray(ivText.value)
                            }
                        } catch (e: Exception) {
                            outputText.value = "Invalid IV: ${e.message}"
                            return@CryptographicTextBox
                        }


                        val key = try {
                            decodeBase64ToSecretKey(keyText.value, selectedAlgorithm.value)
                        } catch (e: Exception) {
                            outputText.value = "Invalid key: ${e.message}"
                            return@CryptographicTextBox
                        }

                        val blockSize = when (selectedAlgorithm.value) {
                            "AES", "Blowfish" -> 16
                            "DES", "3DES" -> 8
                            else -> 1
                        }

                        val paddedInput = if (selectedMode.value.contains("NoPadding"))
                            padTextToBlockSize(inputText.value, blockSize)
                        else
                            inputText.value.toByteArray()

                        val transformation = if (selectedAlgorithm.value == "ChaCha20")
                            "ChaCha20"
                        else
                            "${selectedAlgorithm.value}/${selectedMode.value}"

                        val params = CryptoParams(
                            data = String(paddedInput),
                            key = key,
                            transformation = transformation,
                            iv = if (enableIV.value) iv else null,
                            useBase64 = isBase64Enabled.value
                        )
                        outputText.value = SymmetricBasedAlgorithm().encrypt(params)
                    } catch (e: Exception) {
                        outputText.value = "Encryption failed: ${e.message}"
                    }
                },
            )
        }
    }
}

// -- Helper Functions --


fun getTransformations(context: Context, algorithm: String): List<String> = when (algorithm) {
    "AES" -> context.resources.getStringArray(R.array.aes_transformation_list).toList()
    "DES" -> context.resources.getStringArray(R.array.des_transformation_list).toList()
    "3DES" -> context.resources.getStringArray(R.array.desede_transformation_list).toList()
    "Blowfish" -> context.resources.getStringArray(R.array.blowfish_transformation_list).toList()
    "ChaCha20" -> listOf("ChaCha20")
    else -> emptyList()
}

fun getKeySizes(context: Context, algorithm: String): List<String> = when (algorithm) {
    "AES" -> context.resources.getStringArray(R.array.aes_keysize_list).toList()
    "DES" -> context.resources.getStringArray(R.array.des_keysize_list).toList()
    "3DES" -> context.resources.getStringArray(R.array.desede_keysize_list).toList()
    "Blowfish" -> context.resources.getStringArray(R.array.blowfish_keysize_list).toList()
    "ChaCha20" -> context.resources.getStringArray(R.array.chacha_keysize_list).toList()
    else -> emptyList()
}

//@Composable
//fun MostUsedAlgorithmsLayout(
//    algorithms: List<String>,
//    modifier: Modifier = Modifier
//) {
//    Column (
//        modifier = modifier
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            algorithms.take(3).forEach { algorithm ->
//                AlgorithmCard(algorithm)
//            }
//        }
//    }
//}

//@Composable
//fun AlgorithmCard(algorithm: String) {
//    Box(
//        modifier = Modifier
//            .width(100.dp)
//            .background(
//                MaterialTheme.colorScheme.onPrimary,
//                shape = RoundedCornerShape(8.dp)
//            )
//            .border(
//                1.dp,
//                color = MaterialTheme.colorScheme.onSurface,
//                shape = RoundedCornerShape(5.dp)
//            )
//            .padding(8.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = algorithm,
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onSurface
//        )
//    }
//}

@Preview
@Composable
fun PreviewEncrypt() {
    CryptXTheme(darkTheme = true) {
        MostUsedAlgo()
    }
}