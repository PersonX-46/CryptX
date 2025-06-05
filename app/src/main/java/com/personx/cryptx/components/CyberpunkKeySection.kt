package com.personx.cryptx.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.personx.cryptx.R

@Composable
fun CyberpunkKeySection(
    modifier: Modifier = Modifier,
    title: String,
    keyText: String,
    onKeyTextChange: (String) -> Unit,
    onGenerateKey: () -> Unit,
    isGenActive: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CyberpunkInputBox(
                value = keyText,
                onValueChange = onKeyTextChange,
                placeholder = stringResource(R.string.enter_encryption_key),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            CyberpunkButton(
                isActive = isGenActive,
                onClick = onGenerateKey,
                icon = Icons.Default.Refresh,
                text = "GEN",
            )
        }
    }
}
