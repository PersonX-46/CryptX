package com.personx.cryptx.screens.settingsscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.personx.cryptx.components.Header

@Composable
fun SettingsScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val context = LocalContext.current
    val cyberGreen = Color(0xFF00FF9D)

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 16.dp else 24.dp
    val spacing = if (isCompact) 16.dp else 24.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.onSurface.copy(0.05f),
                    MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                )
            ))
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Header("SETTINGS", windowSizeClass)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                            )
                        ))
                    .padding(padding)
            ) {
                // Security Section
                CyberpunkSectionTitle("SECURITY PROTOCOLS", cyberGreen)

                // Change PIN Card
                CyberpunkSettingCard(
                    icon = Icons.Default.Lock,
                    title = "Change Security PIN",
                    description = "Update your encryption access code",
                    accentColor = cyberGreen,
                    onClick = {  }
                )

                // Backup Card
                CyberpunkSettingCard(
                    icon = Icons.Default.CloudUpload,
                    title = "Backup Database",
                    description = "Create secure backup of encrypted data",
                    accentColor = cyberGreen,
                    onClick = {  }
                )

                // Restore Card
                CyberpunkSettingCard(
                    icon = Icons.Default.CloudDownload,
                    title = "Restore Database",
                    description = "Recover from previous backup",
                    accentColor = cyberGreen,
                    onClick = {  }
                )

                Spacer(modifier = Modifier.height(spacing))

                // App Section
                CyberpunkSectionTitle("APPLICATION CONFIG", cyberGreen)

                // Theme Card
                CyberpunkSettingCard(
                    icon = Icons.Default.EnhancedEncryption,
                    title = "Algorithm Settings",
                    description = "Set the default encryption algorithms",
                    accentColor = cyberGreen,
                    onClick = { /* Theme selection logic */ }
                )

                // Notifications Card
                CyberpunkSettingCard(
                    icon = Icons.Default.Notifications,
                    title = "Base64 by Default",
                    description = "Use Base64 encoding for all data",
                    accentColor = cyberGreen,
                    onClick = { /* Notification settings */ }
                )

                Spacer(modifier = Modifier.height(spacing))

                // Advanced Section
                CyberpunkSectionTitle("OTHER SETTINGS", cyberGreen)

                // About Card
                CyberpunkSettingCard(
                    icon = Icons.Default.Info,
                    title = "About CryptX",
                    description = "Version 2.3.1 | Build 4729",
                    accentColor = cyberGreen,
                    onClick = { navController.navigate("about") }
                )
            }

            // Change PIN Dialog
            if (false) {
                CyberpunkPinChangeDialog(
                    onDismiss = { },
                    onConfirm = {newpin, somepin ->

                    },
                    accentColor = cyberGreen
                )
            }

            // Backup/Restore Status
            if (false) {
                CyberpunkStatusBanner(
                    message = "",
                    color = if (true) cyberGreen else cyberGreen,
                    onDismiss = { }
                )
            }
        }
    }
}

@Composable
private fun CyberpunkSectionTitle(title: String, color: Color) {
    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    )
}

@Composable
private fun CyberpunkSettingCard(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                    ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
                    }

                        Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

            Icon(
            imageVector = Icons.Default.ChevronRight,
    contentDescription = "Navigate",
    tint = accentColor.copy(alpha = 0.7f)
)
        }
    }
}

@Composable
private fun CyberpunkPinChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    accentColor: Color
) {
    var currentPin = remember { mutableStateOf("") }
    var newPin = remember { mutableStateOf("") }
    var confirmPin = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.Black)
                .border(1.dp, accentColor)
                .padding(24.dp)
        ) {
            Text(
                "SECURE PIN UPDATE",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = accentColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Current PIN
            CyberpunkPinInput(
                value = currentPin.value,
                onValueChange = { if (it.length <= 6) currentPin.value = it },
                label = "CURRENT PIN",
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New PIN
            CyberpunkPinInput(
                value = newPin.value,
                onValueChange = { if (it.length <= 6) newPin.value = it },
                label = "NEW PIN",
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm PIN
            CyberpunkPinInput(
                value = confirmPin.value,
                onValueChange = { if (it.length <= 6) confirmPin.value = it },
                label = "CONFIRM PIN",
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("CANCEL", style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace
                    ))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (newPin == confirmPin && newPin.value.isNotEmpty()) {
                            onConfirm(currentPin.value, newPin.value)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.Black
                    ),
                    enabled = newPin.value.isNotEmpty() && newPin.value == confirmPin.value
                ) {
                    Text("CONFIRM", style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ))
                }
            }
        }
    }
}

@Composable
private fun CyberpunkPinInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    accentColor: Color
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = accentColor,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 8.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .border(1.dp, accentColor)
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(6) { index ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(if (index < value.length) accentColor else Color.Transparent)
                                .border(1.dp, accentColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (index < value.length) {
                                Text("•", color = Color.Black)
                            }
                        }
                        if (index < 5) Spacer(modifier = Modifier.width(8.dp))
                    }
                    innerTextField() // Hidden text field for input
                }
            }
        )
    }
}

@Composable
private fun CyberpunkStatusBanner(
    message: String,
    color: Color,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.2f)
            ),
            border = BorderStroke(1.dp, color)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (color == Color(0xFF00FF9D)) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = color
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = color
                    )
                }
            }
        }
    }
}