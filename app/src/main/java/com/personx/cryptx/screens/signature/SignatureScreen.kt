package com.personx.cryptx.screens.signature

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.Header
import com.personx.cryptx.components.SubTitleBar
import com.personx.cryptx.screens.ReusableOutputBox
import com.personx.cryptx.viewmodel.signature.SignatureToolViewModel
import java.io.File
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignatureToolScreen(
    viewModel: SignatureToolViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val state by viewModel.state.collectAsState()
    val cyberGreen = Color(0xFF00FF9C)
    val darkPanel = Color(0xFF0F1F1C)
    val scope = rememberCoroutineScope()
    val selectedKey = remember { mutableStateOf<Uri?>(null) }
    val selectedTarget = remember { mutableStateOf<Uri?>(null) }
    val selectedSignature = remember { mutableStateOf<Uri?>(null) }

    val keyPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
        ) { uri: Uri? ->
        selectedKey.value = uri
        uri?.let { viewModel.setKeyFile(uriToFile(context, it)) }
    }

    val targetPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        selectedTarget.value = uri
        uri?.let {
            val file = uriToFile(context, it)
            viewModel.setTargetFile(file)
            viewModel.setSignatureFileName(file.name)        }
    }

    val signaturePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        selectedSignature.value = uri
        uri?.let {
            viewModel.setSignatureFile(uriToFile(context, it))
        }
    }

    val showExportDialog = remember { mutableStateOf(false) }
    val keyFileName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Header(R.string.signature_tool_header, windowSizeClass = windowSizeClass)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onSurface.copy(0.02f),
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Mode
            CyberpunkDropdown(
                items = listOf("SIGN", "VERIFY", "GENERATE"),
                selectedItem = state.mode,
                onItemSelected = { viewModel.setMode(it) },
                label = R.string.select_mode
            )

            if (state.mode.lowercase() == "verify" || state.mode.lowercase() == "sign") {
                Spacer(Modifier.height(16.dp))

                // Key file
                CyberpunkButton(
                    text = stringResource(R.string.select_key_file, state.keyLabel.uppercase()),
                    onClick = { keyPicker.launch(arrayOf("*/*")) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.FileCopy
                )

                state.keyFile?.let {
                    selectedKey.value?.lastPathSegment?.let { text ->
                        Text(
                            text = text,
                            color = cyberGreen,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (state.mode.lowercase() == "verify"){
                    Spacer(Modifier.height(16.dp))
                    CyberpunkButton(
                        text = stringResource(R.string.select_signature_file),
                        onClick = {
                            signaturePicker.launch(arrayOf("*/*"))
                                  },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Fingerprint
                    )
                    state.sigFile?.let {
                        selectedSignature.value?.lastPathSegment?.let { text ->
                            Text(
                                text = text,
                                color = cyberGreen,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }


                Spacer(Modifier.height(16.dp))

                // Target file
                CyberpunkButton(
                    text = stringResource(R.string.select_file_to, state.mode.uppercase()),
                    onClick = { targetPicker.launch(arrayOf("*/*")) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.AttachFile
                )

                state.targetFile?.let {
                    selectedTarget.value?.lastPathSegment?.let { text ->
                        Text(
                            text = text,
                            color = cyberGreen,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        selectedTarget.value?.lastPathSegment?.let { targetName ->
                            viewModel.setSignatureFileName(targetName)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Key Preview
                Text(stringResource(R.string.key_datastream), color = cyberGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                ReusableOutputBox(
                    content = state.keyPreview.ifBlank { stringResource(R.string.no_key_data_detected) },
                    windowSizeClass = windowSizeClass,
                )

                Spacer(Modifier.height(24.dp))

                // Execute signing/verifying
                CyberpunkButton(
                    text = stringResource(R.string.execute_sequence, state.mode.uppercase()),
                    onClick = {
                        viewModel.startAction()
                    },
                    icon = Icons.Default.FlashOn,
                    modifier = Modifier.fillMaxWidth(),
                    isActive = !state.loading,
                    )

                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(visible = state.loading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = cyberGreen,
                        trackColor = darkPanel
                    )
                }

                Spacer(Modifier.height(8.dp))

                if (state.resultMessage != null)
                    Text(
                        ">> ${state.resultMessage!!}",
                        color = if (state.success) cyberGreen else Color(0xFFFF3864),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

            }

            if (state.mode.lowercase() == "generate") {
                // --- KEY GENERATOR SECTION ---
                Spacer(Modifier.height(32.dp))
                SubTitleBar(
                    title = state.title,
                    onTitleChange = { value ->
                        viewModel.updateTitle(value)
                    },
                    onClick = {
                        //TODO: Implement Session Key Timeout Check
                        viewModel.refreshKeyPairHistory()
                        navController.navigate("keypair_history")
                    },
                    windowSizeClass = windowSizeClass,
                    titleIcon = Icons.Default.VpnKey,
                    clickableIcon = Icons.Default.History
                )
                Spacer(Modifier.height(8.dp))

                Spacer(Modifier.height(16.dp))

                // PRIVATE KEY OUTPUT
                Text(stringResource(R.string.private_key), color = cyberGreen, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                ReusableOutputBox(
                    content = state.generatedPrivateKey.ifBlank { "NOT GENERATED" },
                    windowSizeClass = windowSizeClass,
                )

                Spacer(Modifier.height(16.dp))

                // PUBLIC KEY OUTPUT
                Text(stringResource(R.string.public_key), color = cyberGreen, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                ReusableOutputBox(
                    content = state.generatedPublicKey.ifBlank { "NOT GENERATED" },
                    windowSizeClass = windowSizeClass,
                )
                Spacer(Modifier.height(32.dp))
                Row (
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberpunkButton(
                        text = stringResource(R.string.gen),
                        onClick = { viewModel.generateKeyPair() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        icon = Icons.Filled.VpnKey,
                        isActive = !state.loading
                    )
                    CyberpunkButton(
                        text = stringResource(R.string.save),
                        onClick = {
                            viewModel.saveGeneratedKeyPair()
                            Toast.makeText(context, "Key pair saved!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        icon = Icons.Default.Save,
                        isActive = !state.loading,
                    )
                    CyberpunkButton(
                        text = stringResource(R.string.export),
                        onClick = {
                            // Call your export logic, example:
                            viewModel.exportKeypairs(state.title)
                            Toast.makeText(context, "Successfully exported", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        icon = Icons.Default.CloudUpload,
                        isActive = !state.loading,
                    )
                }

            }

            if (showExportDialog.value) {
                ExportKeyDialog(
                    currentKeyName = keyFileName.value,
                    onKeyNameChange = { keyFileName.value = it },
                    onConfirm = { name ->
                        showExportDialog.value = false
                        // Call your export logic, example:
                        viewModel.exportKeypairs(keyFileName.value)
                        Toast.makeText(context, "Successfully exported", Toast.LENGTH_SHORT).show()
                    },
                    onDismiss = {
                        showExportDialog.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExportKeyDialog(
    currentKeyName: String,
    onKeyNameChange: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }
    val cybergreen = MaterialTheme.colorScheme.onSurface
    Dialog(onDismissRequest = onDismiss) {
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
                stringResource(R.string.export_key_file),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = currentKeyName,
                onValueChange = {
                    onKeyNameChange(it)
                    error = null
                },
                label = { Text(stringResource(R.string.enter_file_name)) },
                singleLine = true,
                isError = error != null,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cybergreen,
                    unfocusedBorderColor = cybergreen,
                    focusedLabelColor = cybergreen,
                    cursorColor = cybergreen,
                    unfocusedLabelColor = cybergreen,
                    focusedTextColor = cybergreen,
                    unfocusedTextColor = cybergreen,
                    disabledTextColor = cybergreen
                )
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Spacer(Modifier.width(16.dp))

                CyberpunkButton(
                    onClick = {
                        if (currentKeyName.isBlank()) {
                            error = "Filename cannot be empty"
                        } else {
                            onConfirm(currentKeyName.trim())
                        }
                    },
                    icon = Icons.Default.CloudUpload,
                    text = R.string.export
                )
            }
        }
    }
}


fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IOException("Unable to open input stream from URI") as Throwable
    val tempFile = File.createTempFile("picked_", null, context.cacheDir)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}
