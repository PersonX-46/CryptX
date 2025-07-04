package com.personx.cryptx.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.LocalNavController
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.data.FeatureItem
import com.personx.cryptx.viewmodel.HomeScreenViewModel

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel(),
    windowSizeClass: WindowSizeClass
) {

    val context = LocalContext.current
    val navController = LocalNavController.current
    val state = viewModel.state.collectAsState()

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
        FeatureItem(Icons.Default.MoreHoriz, "Coming Soon") {
            Toast.makeText(context, "Feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding),
    ) {
        Header("Cryptography Toolkit", windowSizeClass)

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
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(spacing))

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

            // Custom Change PIN Section
            Column(
                modifier = Modifier
                    .padding(top = spacing * 2)
                    .fillMaxWidth(0.8f)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { viewModel.updateShowPinDialog(true)}
                    .padding(spacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Change PIN",
                    tint = cyberGreen,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Change PIN",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )

                Text(
                    "Update your security PIN",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // Cyberpunk-styled PIN Change Dialog
    if (state.value.showPinDialog) {
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
                        color = cyberGreen,
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
                        isCompact = isCompact,
                    )
                }
            }
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
    val cyberDark = Color(0xFF0A0A12)

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

