package com.personx.cryptx.screens.pinsetup

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkKeypadButton
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.PinSetupViewModel
import com.personx.cryptx.viewmodel.PinSetupViewModelFactory

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PinSetupScreen(
    pinCryptoManager: PinCryptoManager,
    onSetupComplete: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val viewModel: PinSetupViewModel = viewModel(
        factory = PinSetupViewModelFactory(pinCryptoManager)
    )
    val state by viewModel.state.collectAsState()
    val cyberpunkGreen = MaterialTheme.colorScheme.onSurface
    val cyberpunkLight = Color(0xFF1E1E1E)

    // Responsive values
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 16.dp else 24.dp
    val topPadding = if (isCompact) 24.dp else 30.dp
    val spacing = if (isCompact) 12.dp else 16.dp
    val smallSpacing = if (isCompact) 6.dp else 8.dp
    val keypadSpacing = if (isCompact) 16.dp else 24.dp
    val buttonPadding = if (isCompact) 24.dp else 32.dp
    val iconSize = if (isCompact) 40.dp else 48.dp
    val pinDotSize = if (isCompact) 20.dp else 24.dp

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onSetupComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onSurface.copy(0.1f),
                        MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                    )
                )
            )
            .padding(padding),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topPadding)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Security",
                tint = cyberpunkGreen,
                modifier = Modifier.size(iconSize)
            )

            Spacer(modifier = Modifier.height(spacing))

            Text(
                text = if (state.step == 1) "Create Secure PIN" else "Confirm Your PIN",
                style = MaterialTheme.typography.run {
                    if (isCompact) titleLarge else headlineSmall
                }.copy(
                    color = cyberpunkGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(smallSpacing))

            Text(
                text = if (state.step == 1)
                    "Enter a 4-digit PIN for security"
                else
                    "Re-enter your PIN to confirm",
                style = MaterialTheme.typography.run {
                    if (isCompact) bodySmall else bodyMedium
                }.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        // PIN Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(pinDotSize)
                            .padding(4.dp)
                            .background(
                                color = when {
                                    state.step == 1 && index < state.pin.length -> cyberpunkGreen
                                    state.step == 2 && index < state.confirmPin.length -> cyberpunkGreen
                                    else -> cyberpunkLight.copy(alpha = 0.3f)
                                },
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = cyberpunkGreen.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        // Error Message
        state.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.padding(vertical = smallSpacing)
            )
        }

        // Keypad
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(keypadSpacing)
        ) {
            // Row 1-3
            listOf("1 2 3", "4 5 6", "7 8 9").forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
                ) {
                    row.split(" ").forEach { num ->
                        CyberpunkKeypadButton(
                            text = num,
                            onClick = {
                                if (state.step == 1) {
                                    viewModel.event(PinSetupEvent.EnterPin(state.pin + num))
                                } else {
                                    viewModel.event(PinSetupEvent.EnterConfirmPin(state.confirmPin + num))
                                }
                            },
                            color = cyberpunkGreen,
                        )
                    }
                }
            }

            // Bottom row (0 and backspace)
            Row(
                horizontalArrangement = Arrangement.spacedBy(keypadSpacing)
            ) {
                CyberpunkKeypadButton(
                    text = "0",
                    onClick = {
                        if (state.step == 1) {
                            viewModel.event(PinSetupEvent.EnterPin(state.pin + "0"))
                        } else {
                            viewModel.event(PinSetupEvent.EnterConfirmPin(state.confirmPin + "0"))
                        }
                    },
                    color = cyberpunkGreen,
                )

                CyberpunkKeypadButton(
                    icon = Icons.AutoMirrored.Filled.Backspace,
                    onClick = {
                        if (state.step == 1) {
                            viewModel.event(PinSetupEvent.EnterPin(state.pin.dropLast(1)))
                        } else {
                            viewModel.event(PinSetupEvent.EnterConfirmPin(state.confirmPin.dropLast(1)))
                        }
                    },
                    color = cyberpunkGreen,
                )
            }
        }

        // Continue Button
        CyberpunkButton(
            onClick = { viewModel.event(PinSetupEvent.Continue) },
            text = if (state.step == 1) "CONTINUE" else "CONFIRM",
            icon = if (state.step == 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = buttonPadding),
            isActive = when (state.step) {
                1 -> state.pin.length == 4
                2 -> state.confirmPin.length == 4
                else -> false
            },
            isCompact = isCompact
        )
    }
}



@Preview
@Composable
fun PinSetupScreenPreview() {
    CryptXTheme(darkTheme = true) {
        //PinSetupScreen(pinCryptoManager = PinCryptoManager(LocalContext.current)) { }
    }
}
