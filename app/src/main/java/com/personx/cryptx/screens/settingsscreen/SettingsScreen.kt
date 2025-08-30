package com.personx.cryptx.screens.settingsscreen

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.personx.cryptx.PrefsHelper
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.viewmodel.SettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val context = LocalContext.current
    val cyberGreen = Color(0xFF00FF9D)
    val state by viewModel.state.collectAsState()
    val prefs = PrefsHelper(context)
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName ?: "N/A"
    val versionCode = packageInfo.versionCode

    val selectedUri = remember { mutableStateOf<Uri?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            selectedUri.value = uri
        }
    )


    // Optional toast for backup result
    state.backupResult?.let { result ->
        LaunchedEffect(result) {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show()
            viewModel.updateBackupResult(null)
        }
    }

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
                )
            )
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Header(R.string.settings_header, windowSizeClass)

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
                    )
                    .padding(padding)
            ) {
                // === SECURITY ===
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CyberpunkSectionTitle(R.string.security_protocols, cyberGreen)
                    if (state.isLoading) {

                        CircularProgressIndicator(
                            color = cyberGreen,
                            strokeWidth = 4.dp
                        )
                    }
                }

                CyberpunkSettingCard(
                    icon = Icons.Default.Lock,
                    title = R.string.change_security_pin,
                    description = R.string.update_your_encryption_access_code,
                    accentColor = cyberGreen,
                    onClick = { viewModel.updateShowPinDialog(true) }
                )

                CyberpunkSettingCard(
                    icon = Icons.Default.CloudUpload,
                    title = R.string.backup_database,
                    description = R.string.create_secure_backup_of_encrypted_data,
                    accentColor = cyberGreen,
                    onClick = { viewModel.updateShowExportDialog(true) }
                )

                CyberpunkSettingCard(
                    icon = Icons.Default.CloudDownload,
                    title = R.string.restore_database,
                    description = R.string.recover_from_previous_backup,
                    accentColor = cyberGreen,
                    onClick = { viewModel.updateShowImportDialog(true) }
                )

                Spacer(modifier = Modifier.height(spacing))

                // === APP CONFIG ===
                CyberpunkSectionTitle(R.string.application_config, cyberGreen)

                CyberpunkSettingCard(
                    icon = Icons.Default.Notifications,
                    title = R.string.base64_by_default,
                    description = R.string.use_base64_encoding_for_all_data,
                    accentColor = cyberGreen,
                    onClick = { viewModel.updateShowBase64(true) }
                )

                CyberpunkSettingCard(
                    icon = Icons.Default.Notifications,
                    title = R.string.hide_plaintext,
                    description = R.string.hide_plaintext_in_encrypted_history,
                    accentColor = cyberGreen,
                    onClick = { viewModel.updateShowHidePlainTextDialog(true) }
                )

                Spacer(modifier = Modifier.height(spacing))

                // === ADVANCED ===
                CyberpunkSectionTitle(R.string.other_settings, cyberGreen)

                CyberpunkSettingCard(
                    icon = Icons.Default.Info,
                    title = R.string.about_cryptx,
                    description = stringResource(R.string.version_build, versionName, versionCode),
                    accentColor = cyberGreen,
                    onClick = { navController.navigate("about") }
                )
            }

            if (state.showPinDialog) {
                ChangePinDialog(viewModel = viewModel)
            }

            if (state.showImportDialog) {

                ImportBackupDialog(
                    viewModel = viewModel,
                    onDismiss = {
                        viewModel.updateShowImportDialog(false)
                        viewModel.resetState()
                    },
                    onConfirm = { password ->
                        val uri = selectedUri.value
                        if (uri == null) {
                            Toast.makeText(context, context.getString(R.string.no_file_selected), Toast.LENGTH_SHORT).show()
                            return@ImportBackupDialog
                        }

                        viewModel.importBackupFromUri(uri, password) { success ->
                            val message = if (success) "Import successful!" else "❌ Import failed."
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            selectedUri.value = null
                            viewModel.updateShowImportDialog(false)
                        }
                    },
                    launchFilePicker = {
                        selectedUri.value = null
                        viewModel.updateBackupResult(null)
                        importLauncher.launch(arrayOf("*/*"))
                    },
                    selectedFileName = selectedUri.value?.lastPathSegment
                )
            }

            if (state.showExportDialog) {
                ExportBackupDialog(
                    viewModel = viewModel,
                    onDismiss = {
                        viewModel.updateShowExportDialog(false)
                        viewModel.resetState()
                    },
                    onConfirm = { password ->
                        viewModel.launchExportAfterPassword(password) // 🔥 Direct export to Downloads
                        viewModel.updateShowExportDialog(false)
                    }
                )
            }

            if (state.showBase64) {
                Base64Dialog(
                    title = R.string.base64_by_default,
                    value = stringResource(R.string.base64_by_default),
                    switchValue = prefs.showBase64,
                    windowSizeClass,
                    onDismiss = {
                        viewModel.updateShowBase64(false)
                        viewModel.resetState()
                    },
                    onConfirm = { value ->
                        prefs.showBase64 = value
                        viewModel.updateShowBase64(false) // Close dialog after showing
                    },
                )
            }

            if (state.hideShowPlainText) {
                Base64Dialog(
                    title = R.string.hide_plaintext,
                    value = stringResource(R.string.hide_plaintext),
                    switchValue = prefs.hidePlainTextInEncryptedHistory,
                    windowSizeClass,
                    onDismiss = {
                        viewModel.updateShowHidePlainTextDialog(false)
                        viewModel.resetState()
                    },
                    onConfirm = { value ->
                        prefs.hidePlainTextInEncryptedHistory = value
                        viewModel.updateShowHidePlainTextDialog(false) // Close dialog after showing
                    }
                )
            }
        }
    }
}

@Composable
fun CyberpunkSectionTitle(@StringRes title: Int, color: Color, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        modifier = modifier.padding(bottom = 8.dp),
        text = stringResource(title),
        style = MaterialTheme.typography.titleSmall.copy(
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            letterSpacing = 1.sp
        )
    )
}

@Composable
private fun CyberpunkSettingCard(
    icon: ImageVector,
    @StringRes title: Int,
    @StringRes description: Int,
    accentColor: Color,
    onClick: () -> Unit
) {
    CyberpunkSettingCard(
        icon = icon,
        title = title,
        description = stringResource(description),
        accentColor = accentColor,
        onClick = onClick
    )
}

@Composable
private fun CyberpunkSettingCard(
    icon: ImageVector,
    @StringRes title: Int,
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
                    text = stringResource(title),
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
fun ChangePinDialog(
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsState()

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
                    stringResource(R.string.change_passphrase).uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CyberpunkPinField(
                    isPin = false,
                    value = state.value.currentPin?: "",
                    onValueChange = { viewModel.updateCurrentPin(it) },
                    label = R.string.current_passphrase,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                CyberpunkPinField(
                    isPin = false,
                    value = state.value.newPin?: "",
                    onValueChange = { viewModel.updateNewPin(it) },
                    label = R.string.new_pin,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                CyberpunkPinField(
                    isPin = false,
                    value = state.value.confirmPin?: "",
                    onValueChange = { viewModel.updateConfirmPin(it) },
                    label = R.string.confirm_pin,
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
                        Text(
                            stringResource(R.string.cancel),
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
                                    viewModel.updateShowPinDialog(false)

                                    if (success) {
                                        Toast.makeText(context,
                                            context.getString(R.string.pin_changed_successfully), Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context,
                                            context.getString(R.string.failed_to_change_pin_please_try_again), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        } catch (e: Exception) {
                            Toast.makeText(context,
                                context.getString(R.string.error_changing_pin, e.message), Toast.LENGTH_SHORT).show()
                        }
                    },
                    icon = Icons.Default.LockReset,
                    text = R.string.confirm,
                )
            }
        }
    }
}

@Composable
fun Base64Dialog(
    @StringRes title: Int,
    value: String,
    switchValue: Boolean,
    windowSizeClass: WindowSizeClass,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit,
){
    val savedValue = switchValue

    // Initialize with saved value
    val isChecked = remember { mutableStateOf(savedValue) }
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val cyberpunkGreen = MaterialTheme.colorScheme.onSurface

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(12.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                stringResource(title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = if (isCompact) MaterialTheme.typography.bodyMedium.fontSize
                        else MaterialTheme.typography.bodyLarge.fontSize
                    ),
                    fontFamily = FontFamily.Monospace
                )
                Switch(
                    checked = isChecked.value,
                    onCheckedChange = { value ->
                        isChecked.value = value
                    },
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
                    Text("CANCEL", style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace))
                }

                Spacer(modifier = Modifier.width(16.dp))

                CyberpunkButton(
                    onClick = {
                        onConfirm(isChecked.value)
                    },
                    icon = Icons.Default.Save,
                    text = R.string.save_output,
                )
            }
        }
    }
}

@Composable
fun CyberpunkPinField(
    isPin: Boolean,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes label: Int
) {
    val cybergreen = MaterialTheme.colorScheme.onSurface
    var isPasswordVisible by remember { mutableStateOf(false) }

    val visualTransformation = if (isPasswordVisible) {
        VisualTransformation.None
    } else {
        PasswordVisualTransformation()
    }
    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text(text = stringResource(label), fontFamily = FontFamily.Monospace) },
        visualTransformation = visualTransformation,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPin) KeyboardType.Number else KeyboardType.Password
        ),
        trailingIcon = {
            val image = if (isPasswordVisible)
                Icons.Default.Visibility
            else Icons.Default.VisibilityOff

            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(tint = cybergreen , imageVector = image, contentDescription = if (isPasswordVisible) "Hide" else "Show")
            }
        },
        modifier = modifier,
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
}
@Composable
fun ExportBackupDialog(
    viewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val passwordInput = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            Text(
                stringResource(R.string.export_backup),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CyberpunkPinField(
                isPin = false,
                value = passwordInput.value,
                onValueChange = { passwordInput.value = it },
                label = R.string.passphrase,
                modifier = Modifier.fillMaxWidth()
            )

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
                    Text("CANCEL", style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace))
                }

                Spacer(modifier = Modifier.width(16.dp))

                CyberpunkButton(
                    onClick = {
                        val password = passwordInput.value
                        if (password.isNotBlank()) {
                            onConfirm(password)
                        } else {
                            Toast.makeText(context,
                                context.getString(R.string.password_is_required), Toast.LENGTH_SHORT).show()
                        }
                    },
                    icon = Icons.Default.CloudUpload,
                    text = R.string.export,
                )
            }
        }
    }
}


@Composable
fun ImportBackupDialog(
    viewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    launchFilePicker: () -> Unit,
    selectedFileName: String?
) {
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val passwordInput = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(12.dp))
                .padding(24.dp)
        ) {
            Text(
                "IMPORT BACKUP",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CyberpunkPinField(
                isPin = false,
                value = passwordInput.value,
                onValueChange = { passwordInput.value = it },
                label = R.string.passphrase,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CyberpunkButton(
                onClick = launchFilePicker,
                icon = Icons.Default.FolderOpen,
                text = selectedFileName ?: stringResource(R.string.select_backup_file),
            )

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
                    Text(stringResource(R.string.cancel), style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace))
                }

                Spacer(modifier = Modifier.width(16.dp))

                CyberpunkButton(
                    onClick = {
                        val password = passwordInput.value
                        if (password.isNotBlank() && selectedFileName != null) {
                            onConfirm(password)
                        } else {
                            Toast.makeText(context,
                                context.getString(R.string.password_or_file_not_selected), Toast.LENGTH_SHORT).show()
                        }
                    },
                    icon = Icons.Default.CloudDownload,
                    text = R.string.import2,
                )
            }
        }
    }
}


