package com.personx.cryptx.screens.settingsscreen

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val context = LocalContext.current
    val cyberGreen = Color(0xFF00FF9D)
    val state by viewModel.state.collectAsState()

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
                    onClick = { viewModel.updateShowPinDialog(true) }
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
            if (state.showPinDialog) {
                ChangePinDialog(
                    viewModel = viewModel
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
fun CyberpunkPinField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val cyberGreen = Color(0xFF00FF9D)
    val cyberDark = Color.Black.copy(0.3f)

    Column(modifier = modifier) {
        Text(
            text = label,
            color = cyberGreen.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = cyberDark.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 1.dp,
                    color = cyberGreen.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(12.dp),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 26.sp
            ),

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun ChangePinDialog(
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val state = viewModel.state
    Dialog(
            onDismissRequest = { viewModel.updateShowPinDialog(false)}
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(24.dp)
            ) {
                Text(
                    "CHANGE SECURITY PIN",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CyberpunkPinField(
                    value = state.value.currentPin?: "",
                    onValueChange = { viewModel.updateCurrentPin(it) },
                    label = "Current PIN",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                CyberpunkPinField(
                    value = state.value.newPin?: "",
                    onValueChange = { viewModel.updateNewPin(it) },
                    label = "New PIN",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                CyberpunkPinField(
                    value = state.value.confirmPin?: "",
                    onValueChange = { viewModel.updateConfirmPin(it) },
                    label = "Confirm PIN",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.updateShowPinDialog(false) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                    ) {
                        Text("CANCEL",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = FontFamily.Monospace,
                            )
                        )
                    }

                Spacer(Modifier.width(16.dp))

                CyberpunkButton(
                    onClick = {
                        try {
                            viewModel.updatePin(
                                oldPin = state.value.currentPin ?: "",
                                newPin = state.value.newPin?: "",
                                confirmPin = state.value.confirmPin?: "",
                                onResult = { success ->
                                    if (success) {
                                        Toast.makeText(context, "PIN changed successfully!", Toast.LENGTH_SHORT).show()
                                        viewModel.updateShowPinDialog(false)
                                    } else {
                                        Toast.makeText(context, "Failed to change PIN. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error changing PIN: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    icon = Icons.Default.LockReset,
                    text = "CONFIRM",
                )
            }
        }
    }
}