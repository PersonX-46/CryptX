package com.personx.cryptx.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptography.utils.SteganographyUtils
import com.example.cryptography.utils.SteganographyUtils.saveByteArrayToFile
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.Toast
import com.personx.cryptx.ui.theme.CryptXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SteganographyScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cyberpunkGreen = Color(0xFF00FFAA)

    // State variables
    val coverImage = remember { mutableStateOf<Bitmap?>(null) }
    val secretFile = remember { mutableStateOf<ByteArray?>(null) }
    val secretFileName = remember { mutableStateOf("")}
    val outputImage = remember { mutableStateOf<Bitmap?>(null) }
    val extractedFile = remember { mutableStateOf<Pair<String, ByteArray>?>(null) }
    val isEncoding = remember { mutableStateOf(true) }
    val showToast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }

    // File pickers
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    coverImage.value = BitmapFactory.decodeStream(stream)
                }
            }
        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    secretFile.value = stream.readBytes()
                }

                // Extract filename
                val cursor = context.contentResolver.query(it, null, null, null, null)
                cursor?.use { c ->
                    val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (c.moveToFirst() && nameIndex >= 0) {
                        val filename = c.getString(nameIndex)
                        secretFileName.value = filename
                    }
                }
            }
        }
    )

    // Process steganography
    fun processSteganography() {
        scope.launch {
            try {
                if (isEncoding.value) {
                    if (coverImage.value == null) throw Exception("Select cover image")
                    if (secretFile.value == null) throw Exception("Select file to hide")

                    outputImage.value = withContext(Dispatchers.IO) {
                        SteganographyUtils.embedFileInImage(
                            image = coverImage.value!!,
                            fileBytes = secretFile.value!!,
                            fileName = secretFileName.value // You might want to get the original filename
                        ) ?: throw Exception("File too large for selected image")
                    }
                    toastMessage.value = "File hidden successfully!"
                } else {
                    if (coverImage.value == null) throw Exception("Select encoded image")

                    extractedFile.value = withContext(Dispatchers.IO) {
                        SteganographyUtils.extractFileFromImage(coverImage.value!!)
                            ?: throw Exception("No hidden file found")
                    }
                    toastMessage.value = "File extracted successfully!"
                }
                showToast.value = true
            } catch (e: Exception) {
                toastMessage.value = "Error: ${e.message}"
                showToast.value = true
            }
        }
    }

    // Save files
    fun saveImage(bitmap: Bitmap) {
        scope.launch {
            try {
                val saved = withContext(Dispatchers.IO) {
                    SteganographyUtils.saveBitmapToGallery(context, bitmap, secretFileName.value)
                }
                toastMessage.value = if (saved) "Image saved!" else "Failed to save image"
                showToast.value = true
            } catch (e: Exception) {
                toastMessage.value = "Save error: ${e.message}"
                showToast.value = true
            }
        }
    }

    fun saveExtractedFile() {
        extractedFile.value?.let { (fileName, bytes) ->
            scope.launch {
                try {
                    val saved = withContext(Dispatchers.IO) {
                        saveByteArrayToFile(context, bytes, fileName)
                    }
                    toastMessage.value = if (saved) "File saved as $fileName!" else "Failed to save file"
                    showToast.value = true
                } catch (e: Exception) {
                    toastMessage.value = "Save error: ${e.message}"
                    showToast.value = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CyberpunkButton(
                    onClick = { isEncoding.value = true },
                    text = "HIDE",
                    icon = Icons.Default.Lock,
                    isActive = !isEncoding.value
                )

                CyberpunkButton(
                    onClick = { isEncoding.value = false },
                    text = "EXTRACT",
                    icon = Icons.Default.LockOpen,
                    isActive = isEncoding.value
                )
            }

            // Cover Image Section
            Text(
                text = "Cover Image:",
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
                if (coverImage.value != null) {
                    Image(
                        bitmap = coverImage.value!!.asImageBitmap(),
                        contentDescription = "Cover Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Select Cover Image",
                        tint = cyberpunkGreen.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            // File Selection (only in encode mode)
            if (isEncoding.value) {
                Text(
                    text = "File to Hide:",
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
                        text = secretFile.value?.let {
                            "Selected file (${it.size} bytes)"
                        } ?: "Select any file",
                        color = cyberpunkGreen
                    )
                }
            }

            // Process Button
            CyberpunkButton(
                onClick = { processSteganography() },
                text = if (isEncoding.value) "HIDE FILE" else "EXTRACT FILE",
                icon = if (isEncoding.value) Icons.Default.Lock else Icons.Default.LockOpen,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Results Section
            if (isEncoding.value) {
                outputImage.value?.let { bitmap ->
                    Text(
                        text = "Image with hidden file:",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            color = cyberpunkGreen
                        )
                    )

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Result Image",
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
                        onClick = { saveImage(bitmap) },
                        text = "SAVE IMAGE",
                        icon = Icons.Default.Save,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            } else {
                extractedFile.value?.let { (fileName, _) ->
                    Text(
                        text = "Extracted file: $fileName",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            color = cyberpunkGreen
                        )
                    )

                    CyberpunkButton(
                        onClick = { saveExtractedFile() },
                        text = "SAVE FILE",
                        icon = Icons.Default.Save,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        // Toast message
        if (showToast.value) {
            Toast(
                message = toastMessage.value,
            )
        }
    }
}

// Preview
@Preview
@Composable
fun SteganographyScreenPreview() {
    CryptXTheme(darkTheme = true) {
        SteganographyScreen()
    }
}