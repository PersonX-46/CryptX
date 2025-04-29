package com.personx.cryptx.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.R
import com.personx.cryptx.algorithms.AESAlgorithm
import com.personx.cryptx.components.CryptographicTextBox
import com.personx.cryptx.components.MaterialDropdownMenu
import com.personx.cryptx.data.CryptoParams
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.utils.CryptoUtils.encodeByteArrayToString
import com.personx.cryptx.utils.CryptoUtils.generateRandomIV
import com.personx.cryptx.utils.CryptoUtils.generateSecretKey
import com.personx.cryptx.utils.CryptoUtils.padTextToBlockSize

@Composable
fun MostUsedAlgo(){
    val clipboardManager = LocalClipboardManager.current
    val selectedAlgorithm = remember {
        mutableStateOf("AES")
    }
    val mostUsedAlgorithms = listOf(
        "AES",
        "DES",
    )

    val inputText = remember { mutableStateOf("") }
    val outputText = remember { mutableStateOf("") }
    val keyText = remember { mutableStateOf("") }
    val ivText = remember { mutableStateOf("") }
    val selectedMode = remember { mutableStateOf("ECB/NoPadding") }
    val selectedKeySize = remember { mutableIntStateOf(128) }
    val isBase64Enabled = remember { mutableStateOf(false) }
    val ivState = remember { mutableStateOf<ByteArray?>(null) }
    val secretKeyState = remember {
        mutableStateOf(generateSecretKey(selectedAlgorithm.value, selectedKeySize.intValue))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MaterialDropdownMenu(
            modifier = Modifier
                .padding(bottom = 1.dp)
                .padding(horizontal = 16.dp)
                .wrapContentWidth(),
            items = mostUsedAlgorithms,
            onItemSelected = { selectedAlgorithm.value = it },
            label = "Algorithms"
        )
        Spacer(
            modifier = Modifier
                .size(height = 10.dp, width = 0.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selectedAlgorithm.value == "AES") {

                CryptographicTextBox(
                    placeholder1 = "Enter Text to Encrypt",
                    placeholder2 = "The Decrypted Text Will Appear Here",
                    enableTextInput = true,
                    text1 = inputText.value,
                    text2 = outputText.value,
                    onText1Change = { inputText.value = it },
                    checkSwitch = isBase64Enabled.value,
                    onSwtichChange = {
                        isBase64Enabled.value = it
                    },
                    keyText = keyText.value,
                    onKeyTextChange = { keyText.value = it },
                    generateKey = {
                        val secretKey = generateSecretKey("AES", 128)
                        secretKeyState.value = secretKey
                        keyText.value = encodeByteArrayToString(secretKey.encoded).trim()
                    },
                    onSubmit = {
                        try {
                            val input = inputText.value
                            val paddedInput = if (selectedMode.value.contains("NoPadding")) {
                                padTextToBlockSize(input, 16)
                            } else {
                                input.toByteArray()
                            }
                            val params = CryptoParams(
                                data = String(paddedInput),
                                key = secretKeyState.value,
                                transformation = "AES/${selectedMode.value}",
                                iv = if (selectedMode.value.startsWith("CBC")) ivState.value else null,
                                useBase64 = isBase64Enabled.value
                            )
                            val encryptedText = AESAlgorithm().encrypt(params)
                            outputText.value = encryptedText
                        } catch (e: Exception) {
                            outputText.value = "Error: ${selectedMode.value }${e.message}"
                        }

                    },
                    transformationList = stringArrayResource(R.array.aes_transformation_list).toList(),
                    keyList = stringArrayResource(R.array.aes_keysize_list).toList(),
                    ivText = ivText.value,
                    onIvTextChange = { ivText.value = it },
                    generateIV = {
                        val iv = generateRandomIV(16)
                        ivState.value = iv
                        ivText.value = encodeByteArrayToString(iv).trim()
                    },
                    onModeSelected = { selectedMode.value = it },
                    onKeySelected = { selectedKeySize.intValue = it.toInt() },
                    onKeyTextClicked = {
                        clipboardManager.setText(AnnotatedString(keyText.value))
                    },
                    onIvTextClicked = {
                        clipboardManager.setText((AnnotatedString(ivText.value)))
                    },
                )
            }
        }
    }
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