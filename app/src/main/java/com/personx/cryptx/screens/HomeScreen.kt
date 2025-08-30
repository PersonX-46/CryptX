package com.personx.cryptx.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.LocalNavController
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.components.MiniDecryptionHistoryItem
import com.personx.cryptx.components.MiniEncryptionHistoryItem
import com.personx.cryptx.data.FeatureItem
import com.personx.cryptx.database.encryption.DecryptionHistory
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.screens.settingsscreen.CyberpunkSectionTitle
import com.personx.cryptx.viewmodel.SettingsViewModel
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel

@Composable
fun HomeScreen(
    viewModel: SettingsViewModel = viewModel(),
    encryptedViewModel: EncryptionViewModel,
    decryptionViewModel: DecryptionViewModel,
    windowSizeClass: WindowSizeClass
) {

    val context = LocalContext.current
    val navController = LocalNavController.current
    val state = viewModel.state.collectAsState()

    encryptedViewModel.refreshHistory()
    decryptionViewModel.refreshHistory()

    // Cyberpunk colors
    val cyberGreen = Color(0xFF00FF9D)

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 16.dp else 24.dp
    val spacing = if (isCompact) 16.dp else 24.dp
    val bottomPadding = if (isCompact) 70.dp else 80.dp

    val featuredItems = listOf(
        FeatureItem(Icons.Default.Lock, "Encrypt") { navController.navigate("encrypt") },
        FeatureItem(Icons.Filled.LockOpen, "Decrypt") { navController.navigate("decrypt") },
        FeatureItem(Icons.Default.Code, "Hash Generator") { navController.navigate("hashGenerator") },
        FeatureItem(Icons.Default.Search, "Hash Detector") { navController.navigate("hashDetector") },
        FeatureItem(Icons.Default.VisibilityOff, "Steganography") { navController.navigate("steganography") },
        FeatureItem(Icons.Default.VpnKey, "Signature") {
            navController.navigate("signature")
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Header("CRYPTOGRAPHY TOOLKIT", windowSizeClass)

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
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(Modifier.height(spacing))
            CyberpunkSectionTitle(
                "FILE ENCRYPTION VAULT", cyberGreen,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = padding)
                    .align(Alignment.Start)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding)
                    .clickable { navController.navigate("file_vault") },
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, cyberGreen.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Vault",
                        tint = cyberGreen,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = "File Vault (WIP)",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = cyberGreen,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(Modifier.height(spacing))
            CyberpunkSectionTitle(
                "CRYPTOGRAPHY TOOLS ", cyberGreen,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = padding)
                    .align(Alignment.Start)
            )
            // Featured Items Grid
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center,
            ) {
                featuredItems.forEach { item ->
                    FeatureCardButton(
                        icon = item.icon,
                        label = item.label,
                        onClick = item.onClick,
                        windowSizeClass = windowSizeClass,
                        modifier = Modifier
                            .padding(top = 5.dp, end = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
            CyberpunkSectionTitle(
                "LATEST ENCRYPTED VALUES", cyberGreen,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = padding)
                    .align(Alignment.Start)
            )

            EncryptedItems(
                entries = encryptedViewModel.history.value,
                cyberpunkGreen = cyberGreen,
                onItemClick = {
                    navController.navigate("encrypt_history") {
                        popUpTo("home") { inclusive = true } // clears entire backstack
                        launchSingleTop = true
                    }
                },
                windowSizeClass = windowSizeClass
            )

            Spacer(Modifier.height(32.dp))
            CyberpunkSectionTitle(
                "LATEST DECRYPTED VALUES", cyberGreen,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = padding)
                    .align(Alignment.Start)
            )

            DecryptedItems(
                entries = decryptionViewModel.history.value,
                cyberpunkGreen = cyberGreen,
                onItemClick = {
                    navController.navigate("decrypt_history") {
                        popUpTo("home") { inclusive = true } // clears entire backstack
                        launchSingleTop = true
                    }
                },
                windowSizeClass = windowSizeClass
            )

        }
    }

    // Cyberpunk-styled PIN Change Dialog
    if (state.value.showPinDialog) {
        navController.navigate("settings")
    }
}

@Composable
fun EncryptedItems(
    entries: List<EncryptionHistory>,
    cyberpunkGreen: Color,
    onItemClick: (EncryptionHistory) -> Unit,
    windowSizeClass: WindowSizeClass
) {
    if (entries.isEmpty()) {
        EmptyHistoryCard(
            type = "ENCRYPTION",
            cyberpunkGreen = cyberpunkGreen,
            windowSizeClass = windowSizeClass
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(3) { index ->
                if (index < entries.size) {
                    MiniEncryptionHistoryItem(
                        entry = entries[index],
                        cyberpunkGreen = cyberpunkGreen,
                        onItemClick = onItemClick,
                        windowSizeClass = windowSizeClass,
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f).padding(horizontal = 4.dp))
                }
            }
        }
    }
}


@Composable
fun DecryptedItems(
    entries: List<DecryptionHistory>,
    cyberpunkGreen: Color,
    onItemClick: (DecryptionHistory) -> Unit,
    windowSizeClass: WindowSizeClass
) {
    if (entries.isEmpty()) {
        EmptyHistoryCard(
            type = "DECRYPTION",
            cyberpunkGreen = cyberpunkGreen,
            windowSizeClass = windowSizeClass
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(3) { index ->
                if (index < entries.size) {
                    MiniDecryptionHistoryItem(
                        entry = entries[index],
                        cyberpunkGreen = cyberpunkGreen,
                        onItemClick = onItemClick,
                        windowSizeClass = windowSizeClass,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryCard(
    type: String, // "ENCRYPTION" or "DECRYPTION"
    cyberpunkGreen: Color,
    windowSizeClass: WindowSizeClass
) {
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 14.dp else 18.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, cyberpunkGreen.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (type == "ENCRYPTION") "No encrypted history found" else "No decrypted history found",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = cyberpunkGreen.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Try adding some",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}



