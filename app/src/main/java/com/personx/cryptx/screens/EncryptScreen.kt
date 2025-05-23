package com.personx.cryptx.screens


import android.content.ClipData
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.CyberpunkKeySection
import com.personx.cryptx.components.CyberpunkOutputSection
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.EncryptionViewModel
import kotlinx.coroutines.launch

@Composable
fun EncryptScreen(
    viewModel: EncryptionViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val algorithms = stringArrayResource(R.array.supported_algorithms_list).toList()

    val scope = rememberCoroutineScope()

    val state = viewModel.state.value

    // Re-generate key and mode list when algorithm changes
    LaunchedEffect(state.selectedAlgorithm) {
        viewModel.updateAlgorithmAndModeLists(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onSurface.copy(0.05f),
                        MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                    )
                )
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Algorithm Selection
        CyberpunkDropdown(
            items = algorithms,
            selectedItem = state.selectedAlgorithm,
            onItemSelected = { viewModel.updateSelectedAlgorithm(it) },
            label = stringResource(R.string.select_algorithm) ,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (state.selectedAlgorithm != "RSA") {
            // Mode Selection
            CyberpunkDropdown(
                items = state.transformationList,
                selectedItem = state.selectedMode,
                onItemSelected = {
                    viewModel.updateSelectedMode(it)
                },
                label = stringResource(R.string.select_mode),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Key Size Selection
            CyberpunkDropdown(
                items = state.keySizeList,
                selectedItem = state.selectedKeySize.toString(),
                onItemSelected = { viewModel.updateSelectedKeySize(it.toInt()) },
                label = stringResource(R.string.select_key_size),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Input Section
            CyberpunkInputBox(
                value = state.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                placeholder = stringResource(R.string.enter_text_to_encrypt),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Key Section
            CyberpunkKeySection(
                keyText = state.keyText,
                onKeyTextChange = { viewModel.updateKeyText(it) },
                onGenerateKey = {
                    viewModel.generateKey()
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                title = stringResource(R.string.key_section)
            )

            // IV Section (conditionally shown)
            if (state.enableIV) {
//                CyberpunkInputBox(
//                    value = ivText.value,
//                    onValueChange = { ivText.value = it },
//                    placeholder = "Enter IV (or leave blank for random)...",
//                    modifier = Modifier.padding(horizontal = 16.dp)
//                )
                CyberpunkKeySection(
                    keyText = state.ivText,
                    onKeyTextChange = { viewModel.updateIVText(it) },
                    onGenerateKey = {
                        viewModel.generateIV()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = stringResource(R.string.iv_section)
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
                    text = stringResource(R.string.base64_input) ,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Switch(
                    checked = state.isBase64Enabled,
                    onCheckedChange = { viewModel.updateBase64Enabled(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00FFAA),
                        checkedTrackColor = Color(0xFF00FFAA).copy(alpha = 0.5f)
                    )
                )
            }

            // Encrypt Button
            CyberpunkButton(
                onClick = {
                    viewModel.encrypt(context)
                },
                icon = Icons.Default.Lock,
                text = "ENCRYPT",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Output Section
            if (state.outputText.isNotEmpty()) {
                CyberpunkOutputSection(
                    output = state.outputText,
                    onCopy = {
                        scope.launch {
                            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Copied", state.outputText)))
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }

    // Toast notification
    if (state.showCopiedToast) {
        android.widget.Toast.makeText(context, "Output copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
    }
}


fun getTransformations(context: Context, algorithm: String): List<String> = when (algorithm) {
    "AES" -> context.resources.getStringArray(R.array.aes_transformation_list).toList()
    "DES" -> context.resources.getStringArray(R.array.des_transformation_list).toList()
    "DESede" -> context.resources.getStringArray(R.array.desede_transformation_list).toList()
    "Blowfish" -> context.resources.getStringArray(R.array.blowfish_transformation_list).toList()
    "ChaCha20" -> listOf("ChaCha20")
    else -> emptyList()
}

fun getKeySizes(context: Context, algorithm: String): List<String> = when (algorithm) {
    "AES" -> context.resources.getStringArray(R.array.aes_keysize_list).toList()
    "DES" -> context.resources.getStringArray(R.array.des_keysize_list).toList()
    "DESede" -> context.resources.getStringArray(R.array.desede_keysize_list).toList()
    "Blowfish" -> context.resources.getStringArray(R.array.blowfish_keysize_list).toList()
    "ChaCha20" -> context.resources.getStringArray(R.array.chacha_keysize_list).toList()
    else -> emptyList()
}

@Preview
@Composable
fun PreviewEncrypt() {
    CryptXTheme(darkTheme = true) {
        EncryptScreen()
    }
}