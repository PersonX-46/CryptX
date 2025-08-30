package com.personx.cryptx.screens.pinsetup

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkPasswordBox
import com.personx.cryptx.components.PlaceholderInfo
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.viewmodel.PassphraseSetupRepository
import com.personx.cryptx.viewmodel.PassphraseSetupViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PassphraseSetupScreen(
    pinCryptoManager: PinCryptoManager,
    onSetupComplete: () -> Unit,
    windowSizeClass: WindowSizeClass
) {

    val context = LocalContext.current

    val viewModel: PassphraseSetupViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PassphraseSetupViewModel(
                    PassphraseSetupRepository(
                        context = context
                    ),
                    pinCryptoManager = pinCryptoManager
                ) as T
            }
        }
    )

    val state by viewModel.state.collectAsState()
    val cyberpunkGreen = MaterialTheme.colorScheme.onSurface
    val cyberpunkLight = Color(0xFF1E1E1E)

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 16.dp else 24.dp
    val topPadding = if (isCompact) 24.dp else 30.dp
    val spacing = if (isCompact) 12.dp else 16.dp
    val smallSpacing = if (isCompact) 6.dp else 8.dp
    val buttonPadding = if (isCompact) 24.dp else 32.dp
    val iconSize = if (isCompact) 40.dp else 48.dp

    var passwordVisible by remember { mutableStateOf(false) }

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
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.01f)
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
                text = if (state.step == 1)
                    stringResource(R.string.create_secure_passphrase)
                else
                    stringResource(R.string.confirm_your_passphrase),
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
                    stringResource(R.string.enter_secure_passphrase)
                else
                    stringResource(R.string.reenter_secure_passphrase),
                style = MaterialTheme.typography.run {
                    if (isCompact) bodySmall else bodyMedium
                }.copy(
                    color = cyberpunkGreen.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(Modifier.height(50.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {

                // BasicTextField with no border/background (just padding) to match your theme
                val textValue = if (state.step == 1) state.passphrase else state.confirmPassphrase
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.15f)) // subtle background — remove if you want fully transparent
                        .padding(horizontal = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        CyberpunkPasswordBox(
                            value = textValue,
                            onValueChange = { newText ->
                                if (state.step == 1) {
                                    viewModel.event(PassphraseSetupEvent.EnterPassphrase(newText))
                                } else {
                                    viewModel.event(PassphraseSetupEvent.EnterConfirmPassphrase(newText))
                                }
                            },
                            placeholder = if (state.step == 1)
                                stringResource(R.string.set_passphrase)
                            else
                                stringResource(R.string.confirm_passphrase),

                            onDone = {
                                viewModel.event(PassphraseSetupEvent.Continue)
                            }
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

                Spacer(modifier = Modifier.height(8.dp))

                // Hint / strength suggestion (optional)
                Text(
                    text = stringResource(R.string.tip_long_pass),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = cyberpunkGreen.copy(alpha = 0.6f),
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }
        }

        // Passphrase input area


        // Error / loading / spacer
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            PlaceholderInfo(
                icon = Icons.Default.Password,
                title = "Create Passphrase",
                description = "Please enter a secure passphrase to protect your vault.",
            )

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator(color = cyberpunkGreen, strokeWidth = 4.dp)
            }
        }

        // Continue / Confirm button
        CyberpunkButton(
            onClick = { viewModel.event(PassphraseSetupEvent.Continue) },
            text = if (state.step == 1) stringResource(R.string.continue_action) else stringResource(R.string.confirm),
            icon = if (state.step == 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = buttonPadding),
            isActive = when (state.step) {
                1 -> state.passphrase.isNotBlank()
                2 -> state.confirmPassphrase.isNotBlank()
                else -> false
            } && !state.isLoading,
            isCompact = isCompact
        )
    }
}
