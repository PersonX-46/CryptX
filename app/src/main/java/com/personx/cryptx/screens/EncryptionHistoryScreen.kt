package com.personx.cryptx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.DecryptionHistoryItem
import com.personx.cryptx.components.EncryptionHistoryItem
import com.personx.cryptx.database.encryption.DecryptionHistory
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.ui.theme.CryptXTheme

@Composable
fun HistoryScreen(
    history: List<EncryptionHistory>,
    onClick: (EncryptionHistory) -> Unit
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val darkBackground = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E).copy(alpha = 0.8f)

    // Collect history from database

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onSurface.copy(0.05f),
                        MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                    )
                )
            )
            .padding(16.dp)
    ) {

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No encryption history found",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { entry ->
                    EncryptionHistoryItem(
                        entry = entry,
                        cyberpunkGreen = cyberpunkGreen,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    history: List<DecryptionHistory>,
    onClick: (DecryptionHistory) -> Unit = { _ -> }
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val darkBackground = Color(0xFF121212)
    val cardColor = Color(0xFF1E1E1E).copy(alpha = 0.8f)

    // Collect history from database

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onSurface.copy(0.05f),
                        MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                    )
                )
        )
            .padding(16.dp)
    ) {

        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No encryption history found",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { entry ->
                    DecryptionHistoryItem(
                        entry = entry,
                        cyberpunkGreen = cyberpunkGreen,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewEncryptionHistoryItem() {
    CryptXTheme {
        HistoryScreen(
            history = listOf(
                EncryptionHistory(
                    id = 1,
                    algorithm = "AES",
                    transformation = "CBC/PKCS5Padding",
                    timestamp = System.currentTimeMillis(),
                    secretText = "Hello World",
                    encryptedOutput = "Encrypted Text",
                    keySize = 256,
                    key = "wefbouebfqoebfwouebf",
                    iv = "jkbwuifbwioefbwobvob",
                    isBase64 = true,
                )
            )
        ) {

        }
    }
}