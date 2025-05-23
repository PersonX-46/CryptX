package com.personx.cryptx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Header(subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00FFAA).copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.onSurface.copy(0.05f)
                    )
                )
            ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CRYPTX",
            style = MaterialTheme.typography.displayLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF00FFAA),
                shadow = Shadow(
                    color = Color(0xFF00FFAA).copy(alpha = 1f),
                    blurRadius = 20f,
                )
            ),
            modifier = Modifier.padding(top = 42.dp)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FFAA).copy(alpha = 1f)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Preview
@Composable
fun HeaderPreview() {
    Header(subtitle = "Steganography & Hashing")
}