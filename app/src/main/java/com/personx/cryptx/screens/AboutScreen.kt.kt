package com.personx.cryptx.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.Header
import java.util.Calendar

@Composable
fun AboutCryptXScreen(windowSizeClass: WindowSizeClass) {
    val context = LocalContext.current
    val neonGreen = Color(0xFF00FFAA)
    val fadedGreen = Color(0xFF88FFCC)

    Column(modifier = Modifier.fillMaxSize()) {
        // HEADER
        Header("ABOUT CRYPTX", windowSizeClass = windowSizeClass)

        // CONTENT
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Version Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1F1C)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Version 1.3.0", color = neonGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Secure vault for your sensitive data.", color = neonGreen, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                }
            }

            DividerLine(neonGreen)

            // Features
            SectionTitle("> FEATURES", neonGreen)
            Column {
                FeatureItem(icon = Icons.Default.Lock, text = "End-to-End Encryption", color = neonGreen)
                FeatureItem(icon = Icons.Default.Security, text = "Zero-Knowledge Architecture", color = neonGreen)
                FeatureItem(icon = Icons.Default.Fingerprint, text = "Biometric Protection", color = neonGreen)
                FeatureItem(icon = Icons.Default.Code, text = "Open Source Components", color = neonGreen)
            }

            DividerLine(neonGreen)

            // Changelog Summary
            SectionTitle("> CHANGELOG", neonGreen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B1815))
                    .border(BorderStroke(1.dp, neonGreen.copy(alpha = 0.5f)))
                    .padding(12.dp)
            ) {
                Column {
                    Text("- Encrypted backup and restore", color = fadedGreen, fontFamily = FontFamily.Monospace)
                    Text("- Improved biometric fallback", color = fadedGreen, fontFamily = FontFamily.Monospace)
                    Text("- UI polish and stability fixes", color = fadedGreen, fontFamily = FontFamily.Monospace)
                }
            }

            DividerLine(neonGreen)

            // Legal
            SectionTitle("> LEGAL", neonGreen)
            Text("© ${Calendar.getInstance().get(Calendar.YEAR)} CryptX", color = fadedGreen, fontFamily = FontFamily.Monospace, fontSize = 13.sp, modifier = Modifier.padding(bottom = 16.dp))

            CyberpunkButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = "https://opensource.org/licenses".toUri()
                    })
                },
                icon = Icons.Default.Description,
                text = "View Licenses"
            )


            Spacer(modifier = Modifier.height(24.dp))
            DividerLine(neonGreen)

            // Developer Info
            SectionTitle("> DEVELOPER", neonGreen)
            Text("Built with care by personx", color = fadedGreen, fontSize = 13.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 8.dp))

            // GitHub Repo
            SectionTitle("> SOURCE CODE", neonGreen)
            CyberpunkButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = "https://github.com/PersonX-46/CryptX".toUri()
                    })
                },
                icon = Icons.Default.Code,
                text = "GitHub Repository"
            )

            // Full Changelog Button
            CyberpunkButton(
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                        data = "https://github.com/PersonX-46/CryptX/releases".toUri()
                    })
                },
                icon = Icons.Default.List,
                text = "View Full Changelog"
            )
            // Contact
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("> CONTACT", neonGreen)
            Text("Email: personx.dev@protonmail.com", color = fadedGreen, fontSize = 13.sp, fontFamily = FontFamily.Monospace)

            Spacer(modifier = Modifier.height(24.dp))
            DividerLine(neonGreen)
        }
    }
}



@Composable
fun FeatureItem(icon: ImageVector, text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        )
    }
}

@Composable
fun DividerLine(color: Color) {
    Divider(
        thickness = 1.dp,
        color = color.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    )
}

@Composable
fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        color = color,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .shadow(2.dp, ambientColor = color)
    )
}