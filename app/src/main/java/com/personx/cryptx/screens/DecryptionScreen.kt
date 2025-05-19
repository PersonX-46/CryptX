package com.personx.cryptx.screens

import android.content.ClipData
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
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptography.algorithms.SymmetricBasedAlgorithm
import com.example.cryptography.data.CryptoParams
import com.example.cryptography.utils.CryptoUtils.decodeBase64ToSecretKey
import com.example.cryptography.utils.CryptoUtils.decodeStringToByteArray
import com.example.cryptography.utils.CryptoUtils.encodeByteArrayToString
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.CyberpunkKeySection
import com.personx.cryptx.components.CyberpunkOutputSection
import com.personx.cryptx.components.Toast
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.DecryptionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DecryptionScreen(
    viewModel: DecryptionViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    val algorithms = stringArrayResource(R.array.supported_algorithms_list).toList()

    val state = viewModel.state.value
    // Re-generate key and mode list when algorithm changes
    LaunchedEffect(state.selectedAlgorithm) {
        viewModel.updateAlgorithmList(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Algorithm Selection
        CyberpunkDropdown(
            items = algorithms,
            selectedItem = state.selectedAlgorithm,
            onItemSelected = { viewModel.updateSelectedAlgorithm(it) },
            label = stringResource(R.string.select_algorithm),
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
                label = "SELECT MODE",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Input Section
            CyberpunkInputBox(
                value = state.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                placeholder = stringResource(R.string.enter_ciphertext_to_decrypt),
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
                CyberpunkInputBox(
                    value = state.ivText,
                    onValueChange = { viewModel.updateIVText(it) },
                    placeholder = stringResource(R.string.enter_iv_used_for_encryption),
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
                    text = stringResource(R.string.base64_input),
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

            // Decrypt Button
            CyberpunkButton(
                onClick = { viewModel.decrypt(context) },
                icon = Icons.Default.LockOpen,
                text = "DECRYPT",
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