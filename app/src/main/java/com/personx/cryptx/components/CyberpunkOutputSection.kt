package com.personx.cryptx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
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
    output: String,
    onCopy: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.encrypted_output),
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

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
                text = output,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CyberpunkButton(
                onClick = onCopy,
                icon = Icons.Default.ContentCopy,
                text = stringResource(R.string.copy_output),
                modifier = Modifier.padding(top = 16.dp)
            )
            CyberpunkButton(
                onClick = onSave,
                icon = Icons.Default.ContentCopy,
                text = stringResource(R.string.save_output),
                modifier = Modifier.padding(top = 16.dp)
            )
        }


    }
}