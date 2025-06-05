package com.personx.cryptx.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onItemClick: (EncryptionHistory) -> Unit,
    onEditClick: (EncryptionHistory) -> Unit,
    onDeleteClick: (EncryptionHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onItemClick(entry) }
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        ),
        border = BorderStroke(1.dp, cyberpunkGreen.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with algorithm and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Algorithm chip with key size
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(cyberpunkGreen.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${entry.algorithm}/${entry.transformation.take(10)}... • ${entry.keySize} bits",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = cyberpunkGreen,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                        .format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content sections
            HistoryItemSection(
                title = "PLAINTEXT",
                content = entry.secretText,
                maxChars = 24,
                titleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                contentColor = cyberpunkGreen.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            HistoryItemSection(
                title = "ENCRYPTED",
                content = entry.encryptedOutput,
                maxChars = 24,
                titleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer with key and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Key with IV if available
                Column {
                    Text(
                        text = "Key: ${entry.key.take(8)}...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    entry.iv?.let {
                        Text(
                            text = "IV: ${it.take(6)}...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                // Action buttons
                Row {
                    IconButton(
                        onClick = { onEditClick(entry) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = cyberpunkGreen.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { onDeleteClick(entry) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = cyberpunkGreen.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemSection(
    title: String,
    content: String,
    maxChars: Int,
    titleColor: Color,
    contentColor: Color
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = titleColor,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = if (content.length > maxChars) "${content.take(maxChars)}..." else content,
            style = MaterialTheme.typography.bodySmall.copy(
                color = contentColor,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}