package com.personx.cryptx.screens

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.screens.settingsscreen.ImportBackupDialog
import com.personx.cryptx.viewmodel.SettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BackupDecisionScreen(
    viewModel: SettingsViewModel,
    windowSizeClass: WindowSizeClass,
    onSkip: () -> Unit,
    onRestoreDone: () -> Unit
) {
    val lightGreen = MaterialTheme.colorScheme.onSurface
    val state by viewModel.state.collectAsState()
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val iconSize = if (isCompact) 48.dp else 64.dp
    val horizontalPadding = if (isCompact) 16.dp else 32.dp
    val spacing = if (isCompact) 16.dp else 24.dp
    val context = LocalContext.current
    val selectedUri = remember { mutableStateOf<Uri?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            selectedUri.value = uri
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.onSurface.copy(0.06f),
                        MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(R.string.restore_backup_header, windowSizeClass)

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
                    .padding(horizontalPadding)
            ) {
                Column(
                    modifier = Modifier.padding(horizontalPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = "Backup",
                        tint = lightGreen,
                        modifier = Modifier.size(iconSize)
                    )

                    Spacer(modifier = Modifier.height(spacing))

                    Text(
                        text = stringResource(R.string.restore_encrypted_data),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = lightGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.if_you_have_a_previous_backup_file_you_can_restore_your_encrypted_data_now),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = lightGreen,
                            fontFamily = FontFamily.Monospace
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = spacing),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CyberpunkButton(
                            onClick = {
                                viewModel.updateShowImportDialog(true)
                            },
                            icon = Icons.Default.CloudDownload,
                            text = R.string.backup_restore,
                            modifier = Modifier.weight(1f)
                        )

                        CyberpunkButton(
                            onClick = onSkip,
                            icon = Icons.Default.Refresh,
                            text = R.string.backup_skip,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing))

                    Text(
                        text = stringResource(R.string.you_can_also_restore_later_from_settings),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = lightGreen,
                            fontFamily = FontFamily.Monospace
                        ),
                        textAlign = TextAlign.Center
                    )

                    if (state.isLoading) {
                        Box(
                            modifier = Modifier.zIndex(2f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = lightGreen,
                                strokeWidth = 4.dp
                            )
                        }
                    }
                }
            }
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
                        Toast.makeText(context, "No file selected.", Toast.LENGTH_SHORT).show()
                        return@ImportBackupDialog
                    }

                    viewModel.importBackupFromUri(uri, password) { success ->
                        val message = if (success) context.getString(R.string.import_successful) else context.getString(
                            R.string.import_failed
                        )
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        selectedUri.value = null
                        viewModel.updateShowImportDialog(false)
                        if(success)
                            onRestoreDone()
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
    }
}
