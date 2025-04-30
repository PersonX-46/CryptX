package com.personx.cryptx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personx.cryptx.R
import com.personx.cryptx.components.MaterialDropdownMenu
import com.personx.cryptx.components.TransparentEditText
import com.personx.cryptx.ui.theme.CryptXTheme
import java.security.MessageDigest


fun computeHash(input: String, algorithm: String): String {
    return try {
        val digest = MessageDigest.getInstance(algorithm)
        val hashBytes = digest.digest(input.toByteArray())
        hashBytes.joinToString("") { "%02x".format(it) } // Convert bytes to hex
    } catch (e: Exception) {
        "Error: ${e.message}" // Handle unsupported algorithms
    }
}

@Composable
fun HashGeneratorScreen() {
    val context = LocalContext.current
    val selectedAlgorithm = remember { mutableStateOf(context.resources.getStringArray(R.array.supported_hash_algorithms).first()) }
    val inputText = remember { mutableStateOf("") }
    val hash = remember { mutableStateOf("\n" +
            "eabdc805b42f3fe8e1eefc8c10f533be89cf9338a2309fd049ea54e28388007721d19394abe8a97fb3472f9d9305d7223b2e68ad8ca8a004246b75780174b9bc\n") }
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MaterialDropdownMenu(
            items = context.resources.getStringArray(R.array.supported_hash_algorithms).toList(),
            onItemSelected = {
                selectedAlgorithm.value = it
                hash.value = computeHash(inputText.value, it)
                             },
            label = "Algorithms",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp)
        )

        // Hash Display Box
        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.onPrimary, // Dark cyberpunk background
                    shape = RoundedCornerShape(10.dp)
                )
                .border(
                    1.dp,
                    color = Color(0xFF00FFAA), // Neon green border
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(10.dp))
                    .padding(7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = "IV",
                    modifier = Modifier
                        .padding(7.dp)
                        .clickable { }
                        .size(20.dp),
                    tint = Color(0xFF00FFAA) // Neon green icon
                )
                TransparentEditText(
                    modifier = Modifier.weight(1f),
                    text = inputText.value,
                    enabled = true,
                    maxLines = 8,
                    onTextChange = {
                        inputText.value = it
                        hash.value = computeHash(it, selectedAlgorithm.value)
                                   },
                    placeholder = "Enter the text to hash",
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    placeholderStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    // Semi-transparent placeholder
                )
            }
        }

        // Cyberpunk Styled Hash Text
        Text(
            text = hash.value,
            overflow = TextOverflow.Ellipsis,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(18.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
        )

        // Copy Button
        IconButton(
            onClick = { clipboardManager.setText(AnnotatedString(hash.value.trim())) },
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(50))
                .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(50))
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy Hash",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun HashGeneratorScreenPreview() {
    CryptXTheme(darkTheme = true) {
        HashGeneratorScreen()
    }
}