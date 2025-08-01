package com.personx.cryptx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.VpnKey
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
import androidx.compose.ui.unit.dp
import com.personx.cryptx.LocalNavController
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.DecryptionHistoryItem
import com.personx.cryptx.components.EncryptionHistoryItem
import com.personx.cryptx.components.Header
import com.personx.cryptx.components.KeyPairHistoryItem
import com.personx.cryptx.database.encryption.DecryptionHistory
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.database.encryption.KeyPairHistory

@Composable
fun HistoryScreen(
    history: List<EncryptionHistory>,
    onItemClick: (EncryptionHistory) -> Unit,
    onEditClick: (EncryptionHistory) -> Unit,
    onDeleteClick: (EncryptionHistory) -> Unit,
    enableEditing: Boolean = true,
    enableDeleting: Boolean = true,
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
                            windowSizeClass = windowSizeClass,
                            enableEditing = enableEditing,
                            enableDeleting = enableDeleting

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
    chooseFromEncrypt: () -> Unit = { },
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
                    Row {
                        CyberpunkButton(
                            onClick = chooseFromEncrypt,
                            icon = Icons.Default.LockOpen,
                            text = "CHOOSE ENCRYPTS",
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .height(48.dp),
                            isCompact = isCompact
                        )

                        CyberpunkButton(
                            onClick = {navController.navigate("decrypt") },
                            icon = Icons.Default.LockOpen,
                            text = "DECRYPT NOW",
                            modifier = Modifier
                                .height(48.dp),
                            isCompact = isCompact
                        )
                    }

                }
            } else {

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                        .padding(vertical = if (isCompact) 8.dp else 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CyberpunkButton(
                        onClick = chooseFromEncrypt,
                        icon = Icons.Default.LockOpen,
                        text = "CHOOSE ENCRYPTS",
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .height(48.dp),
                        isCompact = isCompact
                    )

                    CyberpunkButton(
                        onClick = {navController.navigate("decrypt") },
                        icon = Icons.Default.LockOpen,
                        text = "DECRYPT NOW",
                        modifier = Modifier
                            .height(48.dp),
                        isCompact = isCompact
                    )
                }

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

@Composable
fun KeyPairHistoryScreen(
    history: List<KeyPairHistory>,
    onItemClick: (KeyPairHistory) -> Unit,
    onEditClick: (KeyPairHistory) -> Unit,
    onDeleteClick: (KeyPairHistory) -> Unit,
    enableEditing: Boolean = true,
    enableDeleting: Boolean = true,
    windowSizeClass: WindowSizeClass
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val itemSpacing = if (isCompact) 5.dp else 8.dp
    val padding = if (isCompact) 16.dp else 24.dp
    val emptyStateIconSize = if (isCompact) 64.dp else 96.dp
    val navController = LocalNavController.current

    Column {
        Header("KEY PAIR HISTORY", windowSizeClass)

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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VpnKey,
                        contentDescription = "No Key Pair History",
                        tint = cyberpunkGreen.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(emptyStateIconSize)
                            .graphicsLayer {
                                rotationZ = if (isCompact) 0f else 5f
                                shadowElevation = 8.dp.toPx()
                            }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "No Key Pairs Saved",
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

                    Text(
                        text = "Generated key pairs will appear here",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(horizontal = padding)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CyberpunkButton(
                        onClick = { navController.navigate("signature_tool") },
                        icon = Icons.Default.Add,
                        text = "GENERATE KEY PAIR",
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
                        KeyPairHistoryItem(
                            entry = item,
                            cyberpunkGreen = cyberpunkGreen,
                            modifier = Modifier.fillMaxWidth(),
                            onItemClick = onItemClick,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick,
                            windowSizeClass = windowSizeClass,
                            enableEditing = enableEditing,
                            enableDeleting = enableDeleting
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))
                    }
                }
            }
        }
    }
}
