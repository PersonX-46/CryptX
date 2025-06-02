package com.personx.cryptx.screens.pinlogin

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
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.personx.cryptx.viewmodel.PinLoginViewModel
import com.personx.cryptx.viewmodel.PinLoginViewModelFactory


@Composable
fun PinLoginScreen(
    pinCryptoManager: PinCryptoManager,
    onLoginSuccess: () -> Unit
) {
    val viewModel: PinLoginViewModel = viewModel(
        factory = PinLoginViewModelFactory(pinCryptoManager)
    )
    val state by viewModel.state.collectAsState()
    val cyberpunkGreen = MaterialTheme.colorScheme.onSurface
    val cyberpunkLight = Color(0xFF1E1E1E)

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
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
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Security",
                tint = cyberpunkGreen,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter Your PIN",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = cyberpunkGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Authenticate to continue",
                style = MaterialTheme.typography.bodyMedium.copy(
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
                            .size(24.dp)
                            .padding(5.dp)
                            .background(
                                color = if (index < state.enteredPin.length) cyberpunkGreen
                                else cyberpunkLight.copy(alpha = 0.3f),
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
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Keypad
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row 1-3
            listOf("1 2 3", "4 5 6", "7 8 9").forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    row.split(" ").forEach { num ->
                        CyberpunkKeypadButton(
                            text = num,
                            onClick = {
                                viewModel.event(PinLoginEvent.EnterPin(state.enteredPin + num))
                            },
                            color = cyberpunkGreen
                        )
                    }
                }
            }

            // Bottom row (0 and backspace)
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CyberpunkKeypadButton(
                    text = "0",
                    onClick = {
                        viewModel.event(PinLoginEvent.EnterPin(state.enteredPin + "0"))
                    },
                    color = cyberpunkGreen
                )

                CyberpunkKeypadButton(
                    icon = Icons.AutoMirrored.Filled.Backspace,
                    onClick = {
                        viewModel.event(PinLoginEvent.EnterPin(state.enteredPin.dropLast(1)))
                    },
                    color = cyberpunkGreen
                )
            }
        }

        // Login Button
        CyberpunkButton(
            onClick = {
                if (state.enteredPin.length == 4 && pinCryptoManager.getRawKeyIfPinValid(state.enteredPin) != null) {
                    viewModel.event(PinLoginEvent.Submit)

                }
            },
            text = "AUTHENTICATE",
            icon = Icons.Default.Lock,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            isActive = state.enteredPin.length == 4
        )
    }
}

@Preview
@Composable
fun PinLoginScreenPreview() {
    CryptXTheme(darkTheme = true) {
        PinLoginScreen(pinCryptoManager = PinCryptoManager(LocalContext.current)) { }
    }
}
