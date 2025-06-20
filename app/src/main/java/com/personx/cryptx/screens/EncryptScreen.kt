package com.personx.cryptx.screens


import android.content.ClipData
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.CyberpunkKeySection
import com.personx.cryptx.components.CyberpunkOutputSection
import com.personx.cryptx.components.Header
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.database.encryption.EncryptionHistory
import com.personx.cryptx.screens.pinlogin.PinLoginScreen
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModelFactory
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModelRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EncryptScreen(
    repository: EncryptionViewModelRepository,
    windowSizeClass: WindowSizeClass
) {
    val factory = remember { EncryptionViewModelFactory(repository) }
    val viewModel: EncryptionViewModel = viewModel(factory = factory)
    val context = LocalContext.current
    val state = viewModel.state.value
    val cyberpunkGreen = Color(0xFF00FFAA)
    val clipboard = LocalClipboard.current
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

    LaunchedEffect(state.selectedAlgorithm) {
        viewModel.updateAlgorithmAndModeLists(context)
    }

    when (state.currentScreen) {
        "main" -> {
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
                            .padding(vertical = 16.dp)
                            ,
                        verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 16.dp)
                    ) {
                        // Algorithm Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onSurface.copy(0.05f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Encryption Algorithm",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = cyberpunkGreen.copy(alpha = 0.8f),
                                            fontSize = if (isCompact) MaterialTheme.typography.labelLarge.fontSize
                                            else MaterialTheme.typography.titleSmall.fontSize
                                        )
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.updatePinPurpose("history")
                                            viewModel.updateCurrentScreen("pin_login")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = "History",
                                            tint = cyberpunkGreen,
                                            modifier = Modifier.size(if (isCompact) 24.dp else 28.dp)
                                        )
                                    }
                                }

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
                                    containerColor = MaterialTheme.colorScheme.onSurface.copy(0.05f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Input Data",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = cyberpunkGreen.copy(alpha = 0.8f),
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
                                    containerColor = MaterialTheme.colorScheme.onSurface.copy(0.05f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Security Parameters",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = cyberpunkGreen.copy(alpha = 0.8f),
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
                                                else MaterialTheme.typography.bodyLarge.fontSize
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
                                onClick = { viewModel.encrypt(context) },
                                icon = Icons.Default.Lock,
                                text = "ENCRYPT",
                                modifier = Modifier.fillMaxWidth(),
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
                                                .copy(0.05f)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "Encrypted Output",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    color = cyberpunkGreen.copy(alpha = 0.8f),
                                                    fontSize = if (isCompact) MaterialTheme.typography.labelLarge.fontSize
                                                    else MaterialTheme.typography.titleSmall.fontSize
                                                )
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            CyberpunkOutputSection(
                                                output = state.outputText,
                                                onCopy = {
                                                    scope.launch {
                                                        clipboard.setClipEntry(
                                                            ClipEntry(
                                                                ClipData.newPlainText(
                                                                    "Copied",
                                                                    state.outputText
                                                                )
                                                            )
                                                        )
                                                    }
                                                    Toast.makeText(
                                                        context,
                                                        "Copied!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                onSave = {
                                                    if (state.pinPurpose != "update") {
                                                        viewModel.updatePinPurpose("save")
                                                        viewModel.updateCurrentScreen("pin_login")
                                                    } else {
                                                        viewModel.updateCurrentScreen("pin_login")
                                                    }
                                                },
                                                isCompact = isCompact
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

        "pin_login" -> {
            PinLoginScreen(
                pinCryptoManager = PinCryptoManager(context),
                onLoginSuccess = { pin: String ->
                    when (state.pinPurpose) {
                        "save" -> {
                            scope.launch {
                                val success = viewModel.insertEncryptionHistory(
                                    id = state.id,
                                    pin = pin,
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
                                    viewModel.updateCurrentScreen("main")
                                    viewModel.refreshHistory(pin)
                                    delay(200)
                                    viewModel.clearOutput()
                                    Toast.makeText(context, "Encryption history saved!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save history.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        "history" -> {
                            scope.launch {
                                viewModel.refreshHistory(pin)
                                viewModel.updateCurrentScreen("history_screen")
                            }
                        }

                        "delete" -> {
                            scope.launch {
                                if (viewModel.itemToDelete != null) {
                                    viewModel.itemToDelete?.let { item ->
                                        viewModel.deleteEncryptionHistory(pin, item)
                                        viewModel.refreshHistory(pin)
                                        viewModel.updateCurrentScreen("history_screen")
                                        Toast.makeText(context, "History deleted!", Toast.LENGTH_SHORT).show()
                                        viewModel.prepareItemToDelete(null)
                                    }
                                }
                            }
                        }

                        "update" -> {
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
                                    viewModel.updateEncryptionHistory(pin, item)
                                    viewModel.refreshHistory(pin)
                                    viewModel.updateCurrentScreen("history_screen")
                                    Toast.makeText(context, "History updated!", Toast.LENGTH_SHORT).show()
                                    viewModel.prepareItemToUpdate(null)
                                }
                            }
                        }
                    }
                },
                windowSizeClass = windowSizeClass
            )
        }

        "history_screen" -> {
            HistoryScreen(
                history = viewModel.history.value,
                onEditClick = { it: EncryptionHistory ->
                    viewModel.updateId(it.id)
                    viewModel.updateSelectedAlgorithm(it.algorithm)
                    viewModel.updateSelectedMode(it.transformation)
                    viewModel.updateSelectedKeySize(it.keySize)
                    viewModel.updateKeyText(it.key)
                    viewModel.updateIVText(it.iv?:"")
                    viewModel.updateInputText(it.secretText)
                    viewModel.updateBase64Enabled(it.isBase64)
                    viewModel.updateOutputText(it.encryptedOutput)
                    viewModel.updatePinPurpose("update")
                    viewModel.updateCurrentScreen("main")
                },
                onDeleteClick = { it: EncryptionHistory ->
                    viewModel.prepareItemToDelete(it)
                    viewModel.updatePinPurpose("delete")
                    viewModel.updateCurrentScreen("pin_login")
                },
                onItemClick = { it: EncryptionHistory ->
                    viewModel.updateSelectedAlgorithm(it.algorithm)
                    viewModel.updateSelectedMode(it.transformation)
                    viewModel.updateSelectedKeySize(it.keySize)
                    viewModel.updateKeyText(it.key)
                    viewModel.updateIVText(it.iv?:"")
                    viewModel.updateInputText(it.secretText)
                    viewModel.updateBase64Enabled(it.isBase64)
                    viewModel.updateOutputText(it.encryptedOutput)
                    viewModel.updateCurrentScreen("main")
                },
                windowSizeClass = windowSizeClass
            )
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