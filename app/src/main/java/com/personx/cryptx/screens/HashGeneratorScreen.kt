package com.personx.cryptx.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.ClipboardManagerHelper
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.Header
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.HashGeneratorViewModel
import kotlinx.coroutines.launch

@Composable
fun HashGeneratorScreen(
    viewModel: HashGeneratorViewModel = viewModel(),
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val clipboardManager = ClipboardManagerHelper(LocalContext.current)
    val scope = rememberCoroutineScope()
    val state = viewModel.state.value

    // Initialize algorithms once
    LaunchedEffect(Unit) {
        val algorithms = context.resources.getStringArray(R.array.supported_hash_algorithms).toList()
        viewModel.setAlgorithms(algorithms)
    }

    // Responsive padding values
    val horizontalPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 32.dp
        else -> 64.dp
    }

    val maxContentWidth = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Dp.Infinity
        WindowWidthSizeClass.Medium -> 600.dp
        else -> 800.dp
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header(
            "HASH GENERATOR",
            windowSizeClass = windowSizeClass
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = maxContentWidth)
                    .padding(horizontal = horizontalPadding)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Algorithm Selector
                CyberpunkDropdown(
                    items = state.algorithms,
                    selectedItem = state.selectedAlgorithm,
                    onItemSelected = { viewModel.updateSelectedAlgorithm(it) },
                    label = stringResource(R.string.select_algorithm),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )

                // Input Box
                CyberpunkInputBox(
                    value = state.inputText,
                    onValueChange = { viewModel.updateInputText(it) },
                    placeholder = stringResource(R.string.enter_text_to_hash),
                    modifier = Modifier.fillMaxWidth(),
//                    minLines = when (windowSizeClass.heightSizeClass) {
//                        WindowHeightSizeClass.Compact -> 3
//                        else -> 5
//                    }
                )

                // Hash Output Section or Placeholder
                if (state.inputText.isNotEmpty()) {
                    HashOutputSection(
                        hash = state.generatedHash,
                        algorithm = state.selectedAlgorithm,
                        onCopy = {
                            scope.launch {
                                clipboardManager.copyTextWithTimeout(state.generatedHash)
                            }
                        },
                        windowSizeClass = windowSizeClass,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    PlaceholderInfo(
                        icon = Icons.Outlined.Code,
                        title = stringResource(R.string.hash_generator_ready),
                        description = stringResource(R.string.enter_text_and_select_algorithm_to_generate_hash),
                        windowSizeClass = windowSizeClass
                    )
                }
            }
        }
    }
}

@Composable
private fun HashOutputSection(
    hash: String,
    algorithm: String,
    onCopy: () -> Unit,
    windowSizeClass: WindowSizeClass,
    modifier: Modifier = Modifier
) {
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    Column(modifier = modifier) {
        // Section Title
        Text(
            text = stringResource(R.string.generated_hash),
            style = MaterialTheme.typography.run {
                if (isCompact) titleLarge else titleMedium
            }.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Algorithm Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = "Algorithm:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = algorithm,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF00FFAA)
                )
            )
        }

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

        // Action Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            // Copy Button
            CyberpunkButton(
                onClick = onCopy,
                icon = Icons.Default.ContentCopy,
                text = stringResource(R.string.copy_hash),
                modifier = Modifier.weight(if (isCompact) 1f else 0.5f)
            )

            if (!isCompact) {
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Hash Length Info
            Text(
                text = "Length: ${hash.length} chars",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .weight(if (isCompact) 1f else 0.5f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun PlaceholderInfo(
    icon: ImageVector,
    title: String,
    description: String,
    windowSizeClass: WindowSizeClass
) {
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF00FFAA).copy(alpha = 0.5f),
            modifier = Modifier.size(if (isCompact) 48.dp else 64.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.run {
                    if (isCompact) titleMedium else titleLarge
                },
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.run {
                    if (isCompact) bodyMedium else bodyLarge
                }.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = if (isCompact) 16.dp else 32.dp)
            )
        }
    }
}
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
fun HashGeneratorScreenPreview() {
    val context = LocalContext.current
    val windowSizeClass = calculateWindowSizeClass(activity = context as Activity)
    CryptXTheme(darkTheme = true) {
        HashGeneratorScreen(
            windowSizeClass =windowSizeClass
        )
    }
}