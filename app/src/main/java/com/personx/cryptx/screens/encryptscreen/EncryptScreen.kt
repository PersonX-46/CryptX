package com.personx.cryptx.screens.encryptscreen


import android.content.Context
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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.personx.cryptx.AppSettingsPrefs
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
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EncryptMainScreen(
    viewModel: EncryptionViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {

    val context = LocalContext.current
    val state = viewModel.state.value
    val cyberpunkGreen = MaterialTheme.colorScheme.onSurface
    val clipboard = ClipboardManagerHelper(LocalContext.current)
    val scope = rememberCoroutineScope()

    // Responsive values
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val horizontalPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 16.dp
        WindowWidthSizeClass.Medium -> 24.dp
        else -> 32.dp
    }
    val maxContentWidth = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Dp.Infinity
        WindowWidthSizeClass.Medium -> 600.dp
        else -> 800.dp
    }
    val cardPadding = if (isCompact) 8.dp else 12.dp
    val spacing = if (isCompact) 8.dp else 12.dp

    val prefs = context.getSharedPreferences(AppSettingsPrefs.NAME, Context.MODE_PRIVATE)
    val savedValue = prefs.getBoolean(AppSettingsPrefs.BASE64_DEFAULT, false)
    viewModel.updateBase64Enabled(savedValue)

    LaunchedEffect(state.selectedAlgorithm) {
        viewModel.updateAlgorithmAndModeLists(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()

                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header("ENCRYPTION", windowSizeClass)
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
                    .widthIn(max = maxContentWidth)
                    .padding(horizontal = horizontalPadding)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 16.dp)
            ) {
                // Algorithm Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
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
                                    navController.navigate("encrypt_pin_handler") {
                                        popUpTo("encrypt") { inclusive = true } // clears entire backstack
                                        launchSingleTop = true
                                    }
                                } else {
                                    viewModel.refreshHistory()
                                    navController.navigate("encrypt_history") {
                                        popUpTo("encrypt") { inclusive = true } // clears entire backstack
                                        launchSingleTop = true
                                    }
                                }
                            },
                            windowSizeClass = windowSizeClass,
                            titleIcon = Icons.Default.Lock,
                            clickableIcon = Icons.Default.History,
                            title = "Encryption Algorithm"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CyberpunkDropdown(
                            items = stringArrayResource(R.array.supported_algorithms_list).toList(),
                            selectedItem = state.selectedAlgorithm,
                            onItemSelected = { viewModel.updateSelectedAlgorithm(it) },
                            label = "Algorithm",
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (state.selectedAlgorithm != "RSA") {
                            Spacer(modifier = Modifier.height(8.dp))

                            CyberpunkDropdown(
                                items = state.transformationList,
                                selectedItem = state.selectedMode,
                                onItemSelected = { viewModel.updateSelectedMode(it) },
                                label = "Mode",
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            CyberpunkDropdown(
                                items = state.keySizeList,
                                selectedItem = state.selectedKeySize.toString(),
                                onItemSelected = { viewModel.updateSelectedKeySize(it.toInt()) },
                                label = "Key Size",
                                modifier = Modifier.fillMaxWidth()
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
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Input Data",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = cyberpunkGreen.copy(alpha = 0.8f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = if (isCompact) MaterialTheme.typography.labelLarge.fontSize
                                    else MaterialTheme.typography.titleSmall.fontSize
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            CyberpunkInputBox(
                                value = state.inputText,
                                onValueChange = { viewModel.updateInputText(it) },
                                placeholder = "Enter text to encrypt...",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(if (isCompact) 100.dp else 120.dp)
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
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Security Parameters",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = cyberpunkGreen.copy(alpha = 0.8f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = if (isCompact) MaterialTheme.typography.labelLarge.fontSize
                                    else MaterialTheme.typography.titleSmall.fontSize
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            CyberpunkKeySection(
                                keyText = state.keyText,
                                onKeyTextChange = { viewModel.updateKeyText(it) },
                                onGenerateKey = { viewModel.generateKey() },
                                title = "Encryption Key",
                                isCompact = isCompact
                            )

                            if (state.enableIV) {
                                Spacer(modifier = Modifier.height(8.dp))

                                CyberpunkKeySection(
                                    keyText = state.ivText,
                                    onKeyTextChange = { viewModel.updateIVText(it) },
                                    onGenerateKey = { viewModel.generateIV() },
                                    title = "Initialization Vector (IV)",
                                    isCompact = isCompact
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Base64 Encoding",
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
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.encrypt(context) },
                        icon = Icons.Default.Lock,
                        text = "ENCRYPT",
                        isCompact = isCompact
                    )

                    // Output Section
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
                                        title = stringResource(R.string.encrypted_output),
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
                                                    navController.navigate("encrypt_pin_handler")
                                                } else {
                                                    navController.navigate("encrypt_pin_handler")
                                                }
                                            }else {
                                                if (state.pinPurpose != "update") {
                                                    viewModel.updatePinPurpose("save")
                                                    scope.launch {
                                                        try {
                                                            val success = viewModel.insertEncryptionHistory(
                                                                id = state.id,
                                                                algorithm = state.selectedAlgorithm,
                                                                transformation = state.selectedMode,
                                                                keySize = state.selectedKeySize,
                                                                key = state.keyText,
                                                                iv = if (state.enableIV) state.ivText else null,
                                                                secretText = state.inputText,
                                                                isBase64 = state.isBase64Enabled,
                                                                encryptedOutput = state.outputText
                                                            )
                                                            if (success) {
                                                                viewModel.refreshHistory()
                                                                delay(200)
                                                                viewModel.clearOutput()
                                                                Toast.makeText(context, "Encryption history saved!", Toast.LENGTH_SHORT).show()
                                                                navController.navigate("encrypt") {
                                                                    popUpTo(0) { inclusive = true } // clears entire backstack
                                                                    launchSingleTop = true
                                                                }

                                                            } else {
                                                                Toast.makeText(context, "Failed to save history.", Toast.LENGTH_SHORT).show()
                                                            }
                                                            viewModel.updatePinPurpose("")
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Error saving history: ${e.message}", Toast.LENGTH_SHORT).show()
                                                            viewModel.updatePinPurpose("")
                                                        }
                                                    }
                                                } else {
                                                    scope.launch {
                                                        val itemToUpdate = viewModel.createEncryptedHistory(
                                                            id = state.id,
                                                            algorithm = state.selectedAlgorithm,
                                                            transformation = state.selectedMode,
                                                            keySize = state.selectedKeySize,
                                                            key = state.keyText,
                                                            iv = state.ivText,
                                                            secretText = state.inputText,
                                                            isBase64 = state.isBase64Enabled,
                                                            encryptedOutput = state.outputText
                                                        )
                                                        viewModel.prepareItemToUpdate(itemToUpdate)
                                                        viewModel.itemToUpdate?.let { item ->
                                                            viewModel.updateEncryptionHistory( item)
                                                            viewModel.refreshHistory()
                                                            Toast.makeText(context, "History updated!", Toast.LENGTH_SHORT).show()
                                                            viewModel.prepareItemToUpdate(null)
                                                        }
                                                        viewModel.updatePinPurpose("")
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

fun getTransformations(context: Context, algorithm: String): List<String> = when (algorithm) {
    "AES" -> context.resources.getStringArray(R.array.aes_transformation_list).toList()
    "DES" -> context.resources.getStringArray(R.array.des_transformation_list).toList()
    "DESede" -> context.resources.getStringArray(R.array.desede_transformation_list).toList()
    "Blowfish" -> context.resources.getStringArray(R.array.blowfish_transformation_list).toList()
    "ChaCha20" -> listOf("ChaCha20")
    else -> emptyList()
}

fun getKeySizes(context: Context, algorithm: String): List<String> = when (algorithm) {
    "AES" -> context.resources.getStringArray(R.array.aes_keysize_list).toList()
    "DES" -> context.resources.getStringArray(R.array.des_keysize_list).toList()
    "DESede" -> context.resources.getStringArray(R.array.desede_keysize_list).toList()
    "Blowfish" -> context.resources.getStringArray(R.array.blowfish_keysize_list).toList()
    "ChaCha20" -> context.resources.getStringArray(R.array.chacha_keysize_list).toList()
    else -> emptyList()
}

@Preview
@Composable
fun PreviewEncrypt() {
    CryptXTheme(darkTheme = true) {
        //EncryptScreen(repository = EncryptionViewModelRepository(LocalContext.current))
    }
}