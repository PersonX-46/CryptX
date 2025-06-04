package com.personx.cryptx.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.personx.cryptx.database.encryption.EncryptionHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EncryptionHistoryItem(
    entry: EncryptionHistory,
    cyberpunkGreen: Color,
    onClick: (entry: EncryptionHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick(entry) },
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, cyberpunkGreen.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Algorithm and Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${entry.algorithm}/${entry.transformation}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = cyberpunkGreen,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        .format(Date(entry.timestamp)),
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Original Text
            Text(
                text = "Original:",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = entry.secretText.take(20) + "...",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // Encrypted Output
            Text(
                text = "Encrypted:",
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = entry.encryptedOutput.take(20) + "...",
                color = cyberpunkGreen.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Key Info
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Key: ${entry.key.take(10)}... (${entry.keySize} bits)",
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}