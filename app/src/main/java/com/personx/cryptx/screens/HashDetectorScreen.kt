package com.personx.cryptx.screens

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptography.utils.HashUtils
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.PlaceholderInfo
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.HashDetectorViewModel
import kotlinx.coroutines.launch

@Composable
fun HashDetector(
    viewModel: HashDetectorViewModel = viewModel()
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input Box
        CyberpunkInputBox(
            value = state.inputHash,
            onValueChange = { viewModel.updateInputHash(it) },
            placeholder = stringResource(R.string.paste_hash_here_to_identify),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Detection Results
        if (state.inputHash.isNotEmpty()) {
            DetectionResultsSection(
                detectedHashes = state.detectedHashes,
                hashInfo = state.hashInfo,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            PlaceholderInfo(
                icon = Icons.Default.Fingerprint,
                title = stringResource(R.string.enter_a_hash_to_identify_its_algorithm),
                description = stringResource(R.string.supports_md5_sha_1_sha_256_bcrypt_argon2_and_more)
            )
        }

        // Copy Button (only shown when there's input)
        if (state.inputHash.isNotEmpty()) {
            CyberpunkButton(
                onClick = {
                    scope.launch {
                        clipboardManager.setClipEntry(
                            ClipEntry(ClipData.newPlainText("Copied", state.inputHash))
                        )
                    }
                },
                icon = Icons.Default.ContentCopy,
                text = stringResource(R.string.copy_hash)
            )
        }
    }
}


@Composable
private fun DetectionResultsSection(
    detectedHashes: List<String>,
    hashInfo: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Detection Title
        Text(
            text = stringResource(R.string.detection_results),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Possible Algorithms
        Text(
            text = stringResource(R.string.possible_algorithms),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FFAA)
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        if (detectedHashes.isEmpty()) {
            Text(
                text = stringResource(R.string.unable_to_identify_hash_type),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontStyle = FontStyle.Italic
                )
            )
        } else {
            Column {
                detectedHashes.forEach { hashType ->
                    Text(
                        text = "• $hashType",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Detailed Information
        Text(
            text = stringResource(R.string.detailed_information),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FFAA)
            ),
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = hashInfo,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview
@Composable
fun HashDetectorPreview() {
    CryptXTheme(darkTheme = true) {
        HashDetector()
    }
}