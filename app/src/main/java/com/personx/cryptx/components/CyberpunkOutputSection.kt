package com.personx.cryptx.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.personx.cryptx.R

@Composable
fun CyberpunkOutputSection(
    @StringRes
    title: Int,
    output: String,
    onCopy: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    showLength: Boolean = true,
    maxOutputLines: Int = 5
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val textColor = MaterialTheme.colorScheme.onSurface
    val subtitleColor = textColor.copy(alpha = 0.7f)

    // Responsive values
    val verticalPadding = if (isCompact) 8.dp else 12.dp
    val horizontalPadding = if (isCompact) 12.dp else 16.dp
    val buttonSpacing = if (isCompact) 8.dp else 16.dp
    val cornerRadius = if (isCompact) 6.dp else 8.dp

    Column(modifier = modifier.fillMaxWidth()) {
        // Header with optional length indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.run {
                    if (isCompact) titleMedium else titleLarge
                }.copy(
                    fontFamily = FontFamily.Monospace,
                    color = subtitleColor
                )
            )

            if (showLength && output.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.chars_amount, output.length),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = cyberpunkGreen.copy(alpha = 0.6f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

        // Output box with scrollable content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .border(
                    1.dp,
                    cyberpunkGreen.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(cornerRadius)
                )
        ) {
            Column(modifier = Modifier.padding(horizontalPadding, verticalPadding)) {
                // Scrollable output text
                Scrollbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = if (isCompact) 120.dp else 150.dp)
                ) {
                    Text(
                        text = output,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            color = textColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Optional copy button inside the box for quick access
                if (isCompact && output.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy_output),
                            tint = cyberpunkGreen
                        )
                    }
                }
            }
        }

        // Action buttons - different layout for compact vs regular
        if (!isCompact || output.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (isCompact) 8.dp else 16.dp),
                horizontalArrangement = if (isCompact) Arrangement.End else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isCompact) {
                    CyberpunkButton(
                        onClick = onCopy,
                        icon = Icons.Default.ContentCopy,
                        text = R.string.copy_output,
                        isCompact = isCompact,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(buttonSpacing))
                }

                CyberpunkButton(
                    onClick = onSave,
                    icon = Icons.Default.Save,
                    text = R.string.save_output,
                    isCompact = isCompact,
                    modifier = if (isCompact) Modifier else Modifier.weight(1f)
                )
            }
        }
    }
}

// Simple scrollbar indicator
@Composable
private fun Scrollbar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.verticalScroll(rememberScrollState())) {
        content()
    }
}
