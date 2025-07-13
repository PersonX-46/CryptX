package com.personx.cryptx.screens.decryptscreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.personx.cryptx.ClipboardManagerHelper
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.CyberpunkKeySection
import com.personx.cryptx.components.CyberpunkOutputSection
import com.personx.cryptx.components.Header
import com.personx.cryptx.components.SubTitleBar
import com.personx.cryptx.crypto.SessionKeyManager
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DecryptionScreen(
    viewModel: DecryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val context = LocalContext.current
    val state = viewModel.state.value
    val clipboard = ClipboardManagerHelper(LocalContext.current)
    val scope = rememberCoroutineScope()
    val cyberpunkGreen = Color(0xFF00FFAA)

    // Responsive values
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 16.dp else 24.dp
    val cardPadding = if (isCompact) 8.dp else 12.dp
    val spacing = if (isCompact) 8.dp else 12.dp
    val inputHeight = if (isCompact) 100.dp else 120.dp

    LaunchedEffect(state.selectedAlgorithm) {
        viewModel.updateAlgorithmList(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header("DECRYPTION", windowSizeClass)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                            )
                        )
                    )
                    .padding(padding)
                    .widthIn(max = if (isCompact) Dp.Infinity else 800.dp),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                // Algorithm Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                    ) {
                        SubTitleBar(
                            onClick = {
                                val sessionKey = SessionKeyManager.getSessionKey()
                                if (sessionKey == null) {
                                    viewModel.updatePinPurpose("history")
                                    navController.navigate("decrypt_pin_handler") {
                                        popUpTo("decrypt") { inclusive = true } // clears entire backstack
                                        launchSingleTop = true
                                    }
                                } else {
                                    viewModel.refreshHistory()
                                    navController.navigate("decrypt_history") {
                                        popUpTo("decrypt") { inclusive = true } // clears entire backstack
                                        launchSingleTop = true
                                    }
                                }
                            },
                            windowSizeClass = windowSizeClass,
                            titleIcon = Icons.Filled.LockOpen,
                            clickableIcon = Icons.Filled.History,
                            title = "Decryption Algorithm"
                        )

                        Spacer(modifier = Modifier.height(spacing))

                        CyberpunkDropdown(
                            items = stringArrayResource(R.array.supported_algorithms_list).toList(),
                            selectedItem = state.selectedAlgorithm,
                            onItemSelected = { viewModel.updateSelectedAlgorithm(it) },
                            label = "Algorithm",
                            modifier = Modifier.fillMaxWidth(),
                            isCompact = isCompact
                        )

                        if (state.selectedAlgorithm != "RSA") {
                            Spacer(modifier = Modifier.height(spacing))

                            CyberpunkDropdown(
                                items = state.transformationList,
                                selectedItem = state.selectedMode,
                                onItemSelected = { viewModel.updateSelectedMode(it) },
                                label = "Cipher Mode",
                                modifier = Modifier.fillMaxWidth(),
                                isCompact = isCompact
                            )
                        }
                    }
                }

                if (state.selectedAlgorithm != "RSA") {
                    // Input Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(modifier = Modifier.padding(cardPadding)) {
                            Text(
                                text = "Encrypted Input",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = cyberpunkGreen.copy(alpha = 0.8f),
                                    fontSize = if (isCompact) MaterialTheme.typography.labelLarge.fontSize
                                    else MaterialTheme.typography.titleSmall.fontSize
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing))

                            CyberpunkInputBox(
                                value = state.inputText,
                                onValueChange = { viewModel.updateInputText(it) },
                                placeholder = "Paste ciphertext here...",
                                modifier = Modifier.height(inputHeight),
                            )
                        }
                    }

                    // Security Parameters Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Column(modifier = Modifier.padding(cardPadding)) {
                            Text(
                                text = "Decryption Keys",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = cyberpunkGreen.copy(alpha = 0.8f),
                                    fontSize = if (isCompact) MaterialTheme.typography.labelLarge.fontSize
                                    else MaterialTheme.typography.titleSmall.fontSize
                                )
                            )

                            Spacer(modifier = Modifier.height(spacing))

                            CyberpunkKeySection(
                                isGenActive = false,
                                keyText = state.keyText,
                                onKeyTextChange = { viewModel.updateKeyText(it) },
                                onGenerateKey = { },
                                title = "Decryption Key",
                                isCompact = isCompact
                            )

                            if (state.enableIV) {
                                Spacer(modifier = Modifier.height(spacing))

                                CyberpunkInputBox(
                                    value = state.ivText,
                                    onValueChange = { viewModel.updateIVText(it) },
                                    placeholder = "Enter Initialization Vector (IV)...",
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }

                            Spacer(modifier = Modifier.height(spacing))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Base64 Encoded Input",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = if (isCompact) MaterialTheme.typography.bodyMedium.fontSize
                                        else MaterialTheme.typography.bodyLarge.fontSize,
                                        fontFamily = FontFamily.Monospace,
                                    )
                                )
                                Switch(
                                    checked = state.isBase64Enabled,
                                    onCheckedChange = { viewModel.updateBase64Enabled(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedBorderColor = cyberpunkGreen,
                                        checkedThumbColor = cyberpunkGreen,
                                        checkedTrackColor = Color.Transparent,
                                        uncheckedTrackColor = Color.Transparent,
                                        uncheckedThumbColor = cyberpunkGreen,
                                        uncheckedBorderColor = cyberpunkGreen
                                    ),
                                    modifier = Modifier.scale(if (isCompact) 1f else 1.1f)
                                )
                            }
                        }
                    }

                    // Action Button
                    CyberpunkButton(
                        onClick = { viewModel.decrypt(context) },
                        icon = Icons.Default.LockOpen,
                        text = "DECRYPT",
                        modifier = Modifier.fillMaxWidth(),
                        isCompact = isCompact
                    )

                    if (state.outputText.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = state.outputText.isNotEmpty(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.onSurface
                                        .copy(0.07f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(cardPadding)) {

                                    CyberpunkOutputSection(
                                        title = "DECRYPTED OUTPUT",
                                        output = state.outputText,
                                        onCopy = {
                                            scope.launch {
                                                clipboard.copyTextWithTimeout(state.outputText)
                                            }
                                            Toast.makeText(
                                                context,
                                                "Copied!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onSave = {
                                            val sessionKey = SessionKeyManager.getSessionKey()
                                            if (sessionKey == null) {
                                                if (state.pinPurpose != "update") {
                                                    viewModel.updatePinPurpose("save")
                                                    navController.navigate("decrypt_pin_handler")
                                                } else {
                                                    navController.navigate("decrypt_pin_handler")
                                                }
                                            }else {
                                                if (state.pinPurpose != "update") {
                                                    scope.launch {
                                                        viewModel.updatePinPurpose("save")
                                                        val success = viewModel.insertDecryptionHistory(
                                                            id = state.id,
                                                            state.selectedAlgorithm,
                                                            state.selectedMode,
                                                            state.keyText,
                                                            state.ivText,
                                                            state.inputText,
                                                            state.isBase64Enabled,
                                                            state.outputText
                                                        )
                                                        if (success) {

                                                            viewModel.refreshHistory()
                                                            delay(200)
                                                            viewModel.clearOutput()
                                                            Toast.makeText(
                                                                context,
                                                                "Decryption history saved!",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        } else {
                                                            Toast.makeText(
                                                                context,
                                                                "Failed to save decryption history",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                } else {
                                                    scope.launch {
                                                        val itemToUpdate = viewModel.createDecryptionHistory(
                                                            id = state.id,
                                                            algorithm = state.selectedAlgorithm,
                                                            transformation = state.selectedMode,
                                                            key = state.keyText,
                                                            iv = state.ivText,
                                                            isBase64 = state.isBase64Enabled,
                                                            encryptedText = state.inputText,
                                                            decryptedOutput = state.outputText
                                                        )
                                                        viewModel.prepareItemToUpdate(itemToUpdate)
                                                        viewModel.itemToUpdate?.let { item ->
                                                            viewModel.updateDecryptionHistory(item)
                                                            viewModel.refreshHistory()
                                                            Toast.makeText(context, "History updated!", Toast.LENGTH_SHORT)
                                                                .show()
                                                            viewModel.prepareItemToUpdate(null)
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewDecryptionScreen() {
    CryptXTheme(darkTheme = true) {
       // DecryptionScreen(DecryptionHistoryRepository(LocalContext.current))
    }
}