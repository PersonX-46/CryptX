package com.personx.cryptx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.PlaceholderInfo
import com.personx.cryptx.components.Toast
import com.personx.cryptx.ui.theme.CryptXTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest

fun computeHash(input: String, algorithm: String): String {
    return try {
        val digest = MessageDigest.getInstance(algorithm)
        val hashBytes = digest.digest(input.toByteArray())
        hashBytes.joinToString("") { "%02x".format(it) } // Convert bytes to hex
    } catch (e: Exception) {
        "Error: ${e.message}" // Handle unsupported algorithms
    }
}

@Composable
fun HashGeneratorScreen() {
    val context = LocalContext.current
    val algorithms = context.resources.getStringArray(R.array.supported_hash_algorithms).toList()
    val selectedAlgorithm = remember { mutableStateOf(algorithms.first()) }
    val inputText = remember { mutableStateOf("") }
    val generatedHash = remember { derivedStateOf {
        computeHash(inputText.value, selectedAlgorithm.value)
    } }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val showCopiedToast = remember { mutableStateOf(false) }

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
            text = "HASH GENERATOR",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF00FFAA)
            ),
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Algorithm Selector
        CyberpunkDropdown(
            items = algorithms,
            selectedItem = selectedAlgorithm.value,
            onItemSelected = { selectedAlgorithm.value = it },
            label = "SELECT ALGORITHM",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Input Box
        CyberpunkInputBox(
            value = inputText.value,
            onValueChange = { inputText.value = it },
            placeholder = "Enter text to hash...",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Hash Output Section
        if (inputText.value.isNotEmpty()) {
            HashOutputSection(
                hash = generatedHash.value,
                algorithm = selectedAlgorithm.value,
                onCopy = {
                    scope.launch {
                        clipboardManager.setText(AnnotatedString(generatedHash.value))
                        showCopiedToast.value = true
                        delay(2000)
                        showCopiedToast.value = false
                    }
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            PlaceholderInfo(
                icon = Icons.Default.Lock,
                title = "HASH GENERATOR READY",
                description = "Enter text and select algorithm to generate hash"
            )
        }
    }

    // Toast notification
    if (showCopiedToast.value) {
        Toast(message = "Hash copied to clipboard!")
    }
}

@Composable
private fun HashOutputSection(
    hash: String,
    algorithm: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Title
        Text(
            text = "GENERATED HASH",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Algorithm Info
        Text(
            text = "Algorithm: $algorithm",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FFAA)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Hash Output Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    Color(0xFF00FFAA).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = hash,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Copy Button
        CyberpunkButton(
            onClick = onCopy,
            icon = Icons.Default.ContentCopy,
            text = "COPY HASH",
            modifier = Modifier.padding(top = 16.dp)
        )

        // Hash Length Info
        Text(
            text = "Hash length: ${hash.length} characters",
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview
@Composable
fun HashGeneratorScreenPreview() {
    CryptXTheme(darkTheme = true) {
        HashGeneratorScreen()
    }
}