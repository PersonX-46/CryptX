package com.personx.cryptx.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.PlaceholderInfo
import com.personx.cryptx.ui.theme.CryptXTheme
import com.example.cryptography.utils.HashIdentifier

@Composable
fun HashDetector() {
    val inputHash = remember { mutableStateOf("") }
    val detectedHashes = remember { mutableStateOf<List<String>>(emptyList()) }
    val hashInfo = remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    // Update detection when input changes
    LaunchedEffect(inputHash.value) {
        detectedHashes.value = HashIdentifier.identifyHash(inputHash.value)
        hashInfo.value = if (detectedHashes.value.isNotEmpty()) {
            HashIdentifier.getHashInfo(detectedHashes.value.first())
        } else {
            "No hash detected"
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
        // Header
        Text(
            text = "HASH IDENTIFIER",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF00FFAA)
            ),
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Input Box
        CyberpunkInputBox(
            value = inputHash.value,
            onValueChange = { inputHash.value = it },
            placeholder = "Paste hash here to identify",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Detection Results
        if (inputHash.value.isNotEmpty()) {
            DetectionResultsSection(
                detectedHashes = detectedHashes.value,
                hashInfo = hashInfo.value,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            PlaceholderInfo(
                icon = Icons.Default.Fingerprint,
                title = "Enter a hash to identify its algorithm",
                description = "Supports: MD5, SHA-1, SHA-256, bcrypt, Argon2, and more"
            )
        }

        // Copy Button (only shown when there's input)
        if (inputHash.value.isNotEmpty()) {
            CyberpunkButton(
                onClick = { clipboardManager.setText(AnnotatedString(inputHash.value)) },
                icon = Icons.Default.ContentCopy,
                text = "COPY HASH"
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
            text = "DETECTION RESULTS",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Possible Algorithms
        Text(
            text = "Possible Algorithms:",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FFAA)
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        if (detectedHashes.isEmpty()) {
            Text(
                text = "Unable to identify hash type",
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
            text = "Detailed Information:",
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