package com.personx.cryptx.screens.pinlogin

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.CyberpunkPasswordBox
import com.personx.cryptx.components.PlaceholderInfo
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.viewmodel.PassphraseLoginViewModelFactory
import com.personx.cryptx.viewmodel.PinLoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PassphraseLoginScreen(
    passphraseCryptoManager: PinCryptoManager,
    onLoginSuccess: (passphrase: String) -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val viewModel: PinLoginViewModel.PassphraseLoginViewModel = viewModel(
        factory = PassphraseLoginViewModelFactory(passphraseCryptoManager)
    )
    val state by viewModel.state.collectAsState()
    val cyberpunkGreen = MaterialTheme.colorScheme.onSurface
    val cyberpunkLight = Color(0xFF1E1E1E)

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val horizontalPadding = if (isCompact) 24.dp else 48.dp

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess(state.passphrase)
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onSurface.copy(0.1f),
                        MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                    )
                )
            )
            .padding(horizontalPadding),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (isCompact) 24.dp else 48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Security",
                tint = cyberpunkGreen,
                modifier = Modifier.size(if (isCompact) 48.dp else 64.dp)
            )

            Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 24.dp))

            Text(
                text = "Enter Your Passphrase",
                style = MaterialTheme.typography.run {
                    if (isCompact) headlineSmall else headlineMedium
                }.copy(
                    color = cyberpunkGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Authenticate to unlock your vault",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(Modifier.height(50.dp))

            // Passphrase input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(cyberpunkLight.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CyberpunkPasswordBox(
                        value = state.passphrase,
                        onValueChange = {
                            viewModel.event(PassphraseLoginEvent.EnterPassphrase(it)) },
                        placeholder = "Enter passphrase...",
                    )

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide" else "Show",
                            tint = cyberpunkGreen
                        )
                    }
                }
            }
        }



        // Error / loading
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            state.error?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            PlaceholderInfo(
                icon = Icons.Default.Lock,
                title = "Unlock App to Continue",
                description = "Please enter your passphrase to access your vault.",
            )

            if (state.isLoading) {
                CircularProgressIndicator(color = cyberpunkGreen, strokeWidth = 4.dp)
            }
        }

        // Login button
        CyberpunkButton(
            onClick = { viewModel.event(PassphraseLoginEvent.Submit) },
            text = "AUTHENTICATE",
            icon = Icons.Default.Lock,
            modifier = Modifier.fillMaxWidth(),
            isActive = state.passphrase.isNotBlank() && !state.isLoading,
            isCompact = isCompact
        )

        if (!isCompact) Spacer(modifier = Modifier.height(32.dp))
    }
}

