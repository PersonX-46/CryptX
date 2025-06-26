package com.personx.cryptx.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.unit.sp
import com.personx.cryptx.R

@Composable
fun CyberpunkKeySection(
    modifier: Modifier = Modifier,
    title: String,
    keyText: String,
    onKeyTextChange: (String) -> Unit,
    onGenerateKey: () -> Unit,
    isGenActive: Boolean = true,
    isCompact: Boolean = false,
    showVisibilityToggle: Boolean = false,
    isTextVisible: Boolean = true,
    onVisibilityToggle: (() -> Unit)? = null
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    // Responsive values
    val verticalSpacing = if (isCompact) 4.dp else 8.dp
    val horizontalSpacing = if (isCompact) 8.dp else 12.dp
    val buttonWidth = if (isCompact) 80.dp else 96.dp
    val labelFontSize = if (isCompact) 14.sp else 16.sp

    Column(modifier = modifier.fillMaxWidth()) {
        // Title + visibility toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(7.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = labelColor,
                ),
                modifier = Modifier.weight(1f)
            )

            if (showVisibilityToggle && onVisibilityToggle != null) {
                IconButton(
                    onClick = onVisibilityToggle,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isTextVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isTextVisible) "Hide key" else "Show key",
                        tint = cyberpunkGreen.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(verticalSpacing))

        // Input + Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CyberpunkInputBox(
                value = keyText,
                onValueChange = onKeyTextChange,
                placeholder = stringResource(R.string.enter_encryption_key),
                modifier = Modifier.weight(1f),
                trailingIcon = {

                }
            )

            Spacer(modifier = Modifier.width(horizontalSpacing))

            CyberpunkButton(
                modifier = Modifier.width(buttonWidth),
                isActive = isGenActive,
                onClick = onGenerateKey,
                icon = Icons.Default.Refresh,
                text = if (isCompact) "GEN" else "GENERATE",
                isCompact = isCompact
            )
        }
    }
}

