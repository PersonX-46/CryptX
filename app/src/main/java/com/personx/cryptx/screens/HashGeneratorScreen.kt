package com.personx.cryptx.screens

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.Header
import com.personx.cryptx.components.PlaceholderInfo
import com.personx.cryptx.components.Toast
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.HashGeneratorViewModel
import kotlinx.coroutines.launch

@Composable
fun HashGeneratorScreen(
    viewModel: HashGeneratorViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val state = viewModel.state.value

    // Initialize algorithms once
    LaunchedEffect(Unit) {
        val algorithms = context.resources.getStringArray(R.array.supported_hash_algorithms).toList()
        viewModel.setAlgorithms(algorithms)
    }

    Column {
        Header("HASH GENERATOR")
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
            // Algorithm Selector
            CyberpunkDropdown(
                items = state.algorithms,
                selectedItem = state.selectedAlgorithm,
                onItemSelected = { viewModel.updateSelectedAlgorithm(it) },
                label = stringResource(R.string.select_algorithm),
                modifier = Modifier.padding(horizontal = 16.dp)
                    .padding(top = 20.dp)
            )

            // Input Box
            CyberpunkInputBox(
                value = state.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                placeholder = stringResource(R.string.enter_text_to_hash),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Hash Output Section
            if (state.inputText.isNotEmpty()) {
                HashOutputSection(
                    hash = state.generatedHash,
                    algorithm = state.selectedAlgorithm,
                    onCopy = {
                        scope.launch {
                            clipboardManager.setClipEntry(ClipEntry(ClipData.newPlainText("Copied", state.generatedHash)))
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                PlaceholderInfo(
                    icon = Icons.Outlined.Code,
                    title = stringResource(R.string.hash_generator_ready),
                    description = stringResource(R.string.enter_text_and_select_algorithm_to_generate_hash)
                )
            }
        }
    }

    // Toast notification
    if (state.showCopiedToast) {
        Toast("Hash copied to clipboard!")
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
            text = stringResource(R.string.generated_hash),
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
            text = stringResource(R.string.copy_hash) ,
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