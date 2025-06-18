package com.personx.cryptx.screens

import android.graphics.BitmapFactory
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.components.Toast
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.steganography.SteganographyViewModel
import kotlinx.coroutines.delay

@Composable
fun SteganographyScreen(
    viewModel: SteganographyViewModel = viewModel(),
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val cyberpunkGreen = Color(0xFF00FFAA)
    val state = viewModel.state.value

    // File pickers
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    viewModel.updateCoverImage(BitmapFactory.decodeStream(stream))
                }
            }
        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bytes = stream.readBytes()
                    // Extract filename
                    val cursor = context.contentResolver.query(it, null, null, null, null)
                    cursor?.use { c ->
                        val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (c.moveToFirst() && nameIndex >= 0) {
                            val filename = c.getString(nameIndex)
                            viewModel.updateSecretFile(bytes, filename)
                        }
                    }
                }
            }
        }
    )

    // Hide toast after delay
    if (state.showToast) {
        LaunchedEffect(state.showToast) {
            delay(2000)
            viewModel.hideToast()
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Header("STEGANOGRAPHY", windowSizeClass)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                        )
                    )
                )
                .padding(top = 12.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CyberpunkButton(
                        onClick = { viewModel.updateIsEncoding(true) },
                        text = stringResource(R.string.hide),
                        icon = Icons.Default.Lock,
                        isActive = !state.isEncoding
                    )

                    CyberpunkButton(
                        onClick = { viewModel.toggleMode() },
                        text = stringResource(R.string.extract),
                        icon = Icons.Default.LockOpen,
                        isActive = state.isEncoding
                    )
                }

                // Cover Image Section
                Text(
                    text = stringResource(R.string.cover_image),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        color = cyberpunkGreen.copy(alpha = 0.8f)
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(
                            width = 1.dp,
                            color = cyberpunkGreen.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.coverImage != null) {
                        Image(
                            bitmap = state.coverImage.asImageBitmap(),
                            contentDescription = stringResource(R.string.cover_image),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = stringResource(R.string.select_cover_image),
                            tint = cyberpunkGreen.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // File Selection (only in encode mode)
                if (state.isEncoding) {
                    Text(
                        text = stringResource(R.string.file_to_hide),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            color = cyberpunkGreen.copy(alpha = 0.8f)
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .border(
                                width = 1.dp,
                                color = cyberpunkGreen.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { filePicker.launch("*/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.secretFile?.let {
                                "Selected file (${it.size} bytes)"
                            } ?: "Select any file",
                            color = cyberpunkGreen
                        )
                    }
                }

                // Process Button
                CyberpunkButton(
                    onClick = { viewModel.processSteganography() },
                    text = if (state.isEncoding) stringResource(R.string.hide_file) else stringResource(
                        R.string.extract_file
                    ),
                    icon = if (state.isEncoding) Icons.Default.Lock else Icons.Default.LockOpen,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )

                // Results Section
                if (state.isEncoding) {
                    state.outputImage?.let { bitmap ->
                        Text(
                            text = stringResource(R.string.image_with_hidden_file),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                color = cyberpunkGreen
                            )
                        )

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(R.string.result_image),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .border(
                                    width = 1.dp,
                                    color = cyberpunkGreen.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )

                        CyberpunkButton(
                            onClick = { viewModel.saveImage(context) },
                            text = stringResource(R.string.save_image),
                            icon = Icons.Default.Save,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                } else {
                    state.extractedFile?.let { (fileName, _) ->
                        Text(
                            text = "Extracted file: $fileName",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                color = cyberpunkGreen
                            )
                        )

                        CyberpunkButton(
                            onClick = { viewModel.saveExtractedFile(context) },
                            text = stringResource(R.string.save_file),
                            icon = Icons.Default.Save,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                }
            }

            // Toast message
            if (state.showToast) {
                Toast(
                    message = state.toastMessage,
                )
            }
        }
    }
}

// Preview
@Preview
@Composable
fun SteganographyScreenPreview() {
    CryptXTheme(darkTheme = true) {
        //SteganographyScreen()
    }
}