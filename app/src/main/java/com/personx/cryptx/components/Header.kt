package com.personx.cryptx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun Header(
    subtitle: String,
    windowSizeClass: WindowSizeClass
) {
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val isMedium = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium

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
            )
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Responsive spacing for the header
        Spacer(modifier = Modifier.height(if (isCompact) 16.dp else 32.dp))

        Text(
            text = "CRYPTX",
            style = MaterialTheme.typography.run {
                when {
                    isCompact -> displayMedium
                    isMedium -> displayLarge
                    else -> displayLarge.copy(fontSize = 48.sp)
                }
            }.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF00FFAA),
                shadow = Shadow(
                    color = Color(0xFF00FFAA).copy(alpha = 1f),
                    blurRadius = 20f,
                ),
                letterSpacing = 0.15.em
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(if (isCompact) 4.dp else 8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.run {
                when {
                    isCompact -> titleMedium
                    isMedium -> titleLarge
                    else -> titleLarge.copy(fontSize = 24.sp)
                }
            }.copy(
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF00FFAA).copy(alpha = 1f),
                letterSpacing = 0.1.em
            ),
            modifier = Modifier
                .padding(bottom = if (isCompact) 16.dp else 24.dp)
                .padding(horizontal = 16.dp)
        )

        // Optional: Add a subtle divider on larger screens
        if (!isCompact) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(1.dp)
                    .background(Color(0xFF00FFAA).copy(alpha = 0.3f))
                    .padding(bottom = 8.dp)
            )
        }
    }
}


@Preview
@Composable
fun HeaderPreview() {
    //Header(subtitle = "Steganography & Hashing")
}