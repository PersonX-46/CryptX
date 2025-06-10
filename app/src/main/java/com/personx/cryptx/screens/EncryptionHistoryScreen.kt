package com.personx.cryptx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.DecryptionHistoryItem
import com.personx.cryptx.components.EncryptionHistoryItem
import com.personx.cryptx.components.Header
import com.personx.cryptx.database.encryption.DecryptionHistory
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.ui.theme.CryptXTheme

@Composable
fun HistoryScreen(
    history: List<EncryptionHistory>,
    onItemClick: (EncryptionHistory) -> Unit,
    onEditClick: (EncryptionHistory) -> Unit,
    onDeleteClick: (EncryptionHistory) -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val itemSpacing = if (isCompact) 5.dp else 8.dp
    val padding = if (isCompact) 16.dp else 24.dp
    val emptyStateIconSize = if (isCompact) 64.dp else 96.dp
    val navController = LocalNavController.current

    Column (
    ) {
        Header("ENCRYPTION HISTORY", windowSizeClass)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                        )
                    )
                )
        ) {


            if (history.isEmpty()) {
                // Beautiful empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                    MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                                )
                            )
                        )
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated icon
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = "Empty History",
                        tint = cyberpunkGreen.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(emptyStateIconSize)
                            .graphicsLayer {
                                rotationZ = if (isCompact) 0f else 5f
                                shadowElevation = 8.dp.toPx()
                            }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Glowing text
                    Text(
                        text = "No History Yet",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = cyberpunkGreen,
                            fontFamily = FontFamily.Monospace,
                            shadow = Shadow(
                                color = cyberpunkGreen.copy(alpha = 0.3f),
                                offset = Offset(0f, 0f),
                                blurRadius = 8f
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = "Your encrypted items will appear here",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = padding)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action button
                    CyberpunkButton(
                        onClick = {navController.navigate("encrypt")},
                        icon = Icons.Default.LockOpen,
                        text = "ENCRYPT NOW",
                        modifier = Modifier
                            .padding(horizontal = 48.dp)
                            .height(48.dp),
                        isCompact = isCompact
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = padding),
                    verticalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    items(history) { item ->
                        EncryptionHistoryItem(
                            entry = item,
                            cyberpunkGreen = cyberpunkGreen,
                            modifier = Modifier.fillMaxWidth(),
                            onItemClick = onItemClick,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick,
                            windowSizeClass = windowSizeClass
                        )
                    }

                    // Add bottom spacer
                    item {
                        Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryScreen(
    history: List<DecryptionHistory>,
    onItemClick: (DecryptionHistory) -> Unit = { _ -> },
    onEditClick: (DecryptionHistory) -> Unit,
    onDeleteClick: (DecryptionHistory) -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val itemSpacing = if (isCompact) 12.dp else 16.dp
    val padding = if (isCompact) 16.dp else 24.dp
    val emptyStateIconSize = if (isCompact) 64.dp else 96.dp
    val navController = LocalNavController.current


    Column {
        Header("DECRYPTION HISTORY", windowSizeClass)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                        )
                    )
                )
        ) {
            if (history.isEmpty()) {
                // Beautiful empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                    MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                                )
                            )
                        )
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated icon
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = "Empty History",
                        tint = cyberpunkGreen.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(emptyStateIconSize)
                            .graphicsLayer {
                                rotationZ = if (isCompact) 0f else 5f
                                shadowElevation = 8.dp.toPx()
                            }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Glowing text
                    Text(
                        text = "No History Yet",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = cyberpunkGreen,
                            fontFamily = FontFamily.Monospace,
                            shadow = Shadow(
                                color = cyberpunkGreen.copy(alpha = 0.3f),
                                offset = Offset(0f, 0f),
                                blurRadius = 8f
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = "Your decrypted items will appear here",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = padding)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action button
                    CyberpunkButton(
                        onClick = {navController.navigate("decrypt") },
                        icon = Icons.Default.LockOpen,
                        text = "DECRYPT NOW",
                        modifier = Modifier
                            .padding(horizontal = 48.dp)
                            .height(48.dp),
                        isCompact = isCompact
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = padding),
                    verticalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    items(history) { item ->
                        DecryptionHistoryItem(
                            entry = item,
                            cyberpunkGreen = cyberpunkGreen,
                            modifier = Modifier.fillMaxWidth(),
                            onItemClick = onItemClick,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick,
                            windowSizeClass = windowSizeClass
                        )
                    }

                    // Add bottom spacer
                    item {
                        Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))
                    }
                }
            }
        }
    }


}

@Preview
@Composable
fun PreviewEncryptionHistoryItem() {
    CryptXTheme {
//        HistoryScreen(
//            history = listOf(
//                EncryptionHistory(
//                    id = 1,
//                    algorithm = "AES",
//                    transformation = "CBC/PKCS5Padding",
//                    timestamp = System.currentTimeMillis(),
//                    secretText = "Hello World",
//                    encryptedOutput = "Encrypted Text",
//                    keySize = 256,
//                    key = "wefbouebfqoebfwouebf",
//                    iv = "jkbwuifbwioefbwobvob",
//                    isBase64 = true,
//                )
//            ),
//            onItemClick = {  },
//            onEditClick = {  },
//        ) {
//
//        }
    }
}