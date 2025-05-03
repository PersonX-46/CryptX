package com.personx.cryptx.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.ui.theme.CryptXTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//@Composable
//fun SteganographyScreen() {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    // State variables
//    val selectedImage = remember { mutableStateOf<Uri?>(null) }
//    val secretMessage = remember { mutableStateOf("") }
//    val outputImage = remember { mutableStateOf<Bitmap?>(null) }
//    val isEncoding = remember { mutableStateOf(true) }
//    val showToast = remember { mutableStateOf(false) }
//    val toastMessage = remember { mutableStateOf("") }
//
//    // Image picker
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent(),
//        onResult = { uri -> selectedImage.value = uri }
//    )
//
//    // Handle encoding/decoding
//    fun processSteganography() {
//        scope.launch {
//            try {
//                if (isEncoding.value) {
//                    if (selectedImage.value == null) throw Exception("Select an image first")
//                    if (secretMessage.value.isEmpty()) throw Exception("Enter a secret message")
//
//                    // Encode message into image (implementation needed)
//                    outputImage.value = encodeMessageToImage(
//                        context = context,
//                        imageUri = selectedImage.value!!,
//                        message = secretMessage.value
//                    )
//                    toastMessage.value = "Message encoded successfully!"
//                } else {
//                    if (selectedImage.value == null) throw Exception("Select an encoded image")
//
//                    // Decode message from image (implementation needed)
//                    secretMessage.value = decodeMessageFromImage(
//                        context = context,
//                        imageUri = selectedImage.value!!
//                    )
//                    toastMessage.value = "Message decoded successfully!"
//                }
//                showToast.value = true
//            } catch (e: Exception) {
//                toastMessage.value = "Error: ${e.message}"
//                showToast.value = true
//            }
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(
//                        Color(0xFF0A0A12),
//                        Color(0xFF12121A)
//                    )
//                )
//            )
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//                .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            // Header
//            Text(
//                text = "STEGANOGRAPHY",
//                style = MaterialTheme.typography.headlineLarge.copy(
//                    fontFamily = FontFamily.Monospace,
//                    fontWeight = FontWeight.ExtraBold,
//                    color = Color(0xFF00FFAA)
//                ),
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//
//            // Mode Toggle
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                CyberpunkButton(
//                    onClick = { isEncoding.value = true },
//                    text = "ENCODE",
//                    icon = Icons.Default.Lock,
//                    isActive = isEncoding.value
//                )
//
//                CyberpunkButton(
//                    onClick = { isEncoding.value = false },
//                    text = "DECODE",
//                    icon = Icons.Default.LockOpen,
//                    isActive = !isEncoding.value
//                )
//            }
//
//            // Image Selection
//            CyberpunkInputBox(
//                value = selectedImage.value?.toString() ?: "",
//                onValueChange = {},
//                placeholder = "Select an image",
//                trailingIcon = {
//                    IconButton(
//                        onClick = { launcher.launch("image/*") }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Image,
//                            contentDescription = "Select Image",
//                            tint = Color(0xFF00FFAA),
//                        )
//                    }
//                },
//            )
//
//            // Image Preview
//            selectedImage.value?.let { uri ->
//                Image(
//                    painter = rememberImagePainter(uri),
//                    contentDescription = "Selected Image",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .border(
//                            width = 1.dp,
//                            color = Color(0xFF00FFAA).copy(alpha = 0.5f),
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                        .padding(4.dp),
//                    contentScale = ContentScale.Fit
//                )
//            }
//
//            // Secret Message (for encode) or Result (for decode)
//            if (isEncoding.value) {
//                CyberpunkInputBox(
//                    value = secretMessage.value,
//                    onValueChange = { secretMessage.value = it },
//                    placeholder = "Enter secret message",
//                    modifier = Modifier.fillMaxWidth(),
//                    //maxLines = 3
//                )
//            } else {
//                Text(
//                    text = if (secretMessage.value.isNotEmpty()) "Hidden message: ${secretMessage.value}"
//                    else "No message decoded yet",
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        fontFamily = FontFamily.Monospace,
//                        color = Color(0xFF00FFAA).copy(alpha = 0.8f)
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                )
//            }
//
//            // Process Button
//            CyberpunkButton(
//                onClick = { processSteganography() },
//                text = if (isEncoding.value) "ENCODE MESSAGE" else "DECODE MESSAGE",
//                icon = if (isEncoding.value) Icons.Default.Lock else Icons.Default.LockOpen,
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//
//            // Output Image (for encode)
//            outputImage.value?.let { bitmap ->
//                Text(
//                    text = "Encoded Image:",
//                    style = MaterialTheme.typography.bodyLarge.copy(
//                        fontFamily = FontFamily.Monospace,
//                        color = Color(0xFF00FFAA)
//                    ),
//                    modifier = Modifier.padding(top = 16.dp)
//                )
//
//                Image(
//                    bitmap = bitmap.asImageBitmap(),
//                    contentDescription = "Encoded Image",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .border(
//                            width = 1.dp,
//                            color = Color(0xFF00FFAA).copy(alpha = 0.5f),
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                )
//
//                CyberpunkButton(
//                    onClick = {
//                        // Save image implementation
//                        //saveImageToGallery(context, bitmap)
//                        toastMessage.value = "Image saved to gallery!"
//                        showToast.value = true
//                    },
//                    text = "SAVE IMAGE",
//                    icon = Icons.Default.Save,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                )
//            }
//        }
//
//        // Toast message
//        if (showToast.value) {
//            Toast(
//                message = toastMessage.value,
//                onDismiss = { showToast.value = false }
//            )
//        }
//    }
//}
//
//// Custom Toast Component
//@Composable
//fun Toast(message: String, onDismiss: () -> Unit) {
//    val animate = rememberInfiniteTransition().animateFloat(
//        initialValue = 0.3f,
//        targetValue = 0.7f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1000, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        )
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(bottom = 100.dp),
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        Text(
//            text = message,
//            style = MaterialTheme.typography.bodyMedium.copy(
//                fontFamily = FontFamily.Monospace,
//                color = Color.White
//            ),
//            modifier = Modifier
//                .background(
//                    color = Color(0xFF00FFAA).copy(alpha = animate.value),
//                    shape = RoundedCornerShape(8.dp)
//                )
//                .padding(horizontal = 24.dp, vertical = 12.dp)
//        )
//
//        LaunchedEffect(Unit) {
//            delay(3000)
//            onDismiss()
//        }
//    }
//}
//
//// Preview
//@Preview
//@Composable
//fun SteganographyScreenPreview() {
//    CryptXTheme(darkTheme = true) {
//        SteganographyScreen()
//    }
//}