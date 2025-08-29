package com.personx.cryptx.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.personx.cryptx.R
import com.personx.cryptx.components.Header
import java.util.Calendar

@Composable
fun AboutCryptXScreen(windowSizeClass: WindowSizeClass) {
    val context = LocalContext.current
    val cybergreen = MaterialTheme.colorScheme.onSurface
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName ?: "N/A"

    Column(modifier = Modifier.fillMaxSize()) {
        // HEADER
        Header(R.string.about_header, windowSizeClass = windowSizeClass)

        // CONTENT
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
                .verticalScroll(rememberScrollState())
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
                    Text(
                        "Version $versionName",
                        color = cybergreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.lightweight_vault_for_encrypted_notes_hidden_files_and_hash_tools),
                        color = cybergreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }

// Features
            SectionTitle("> FEATURES", cybergreen)
            Column {
                FeatureItem(Icons.Default.Lock,
                    stringResource(R.string.aes_text_encryption), cybergreen)
                FeatureItem(Icons.Default.Image,
                    stringResource(R.string.lsb_image_steganography), cybergreen)
                FeatureItem(Icons.Default.Fingerprint,
                    stringResource(R.string.hash_generator_detector), cybergreen)
                FeatureItem(Icons.Default.Security,
                    stringResource(R.string._100_offline_no_cloud), cybergreen)
            }

            DividerLine(cybergreen)

            // Changelog
            SectionTitle("> CHANGELOG", cybergreen)
            ClickableLink(
                label = "View Full Changelog",
                url = "https://github.com/PersonX-46/CryptX/releases",
                color = cybergreen,
                icon = Icons.AutoMirrored.Filled.List
            )

            DividerLine(cybergreen)

            // Legal
            SectionTitle("> LEGAL", cybergreen)
            Text("© ${Calendar.getInstance().get(Calendar.YEAR)} CryptX", color = cybergreen, fontFamily = FontFamily.Monospace, fontSize = 13.sp, modifier = Modifier.padding(bottom = 16.dp))
            ClickableLink(
                label = stringResource(R.string.view_licenses),
                url = "https://opensource.org/licenses",
                color = cybergreen,
                icon = Icons.Default.Description
            )

            DividerLine(cybergreen)

            // Developer
            SectionTitle(stringResource(R.string.developer), cybergreen)
            Text(stringResource(R.string.built_with_care_by_personx), color = cybergreen, fontSize = 13.sp, fontFamily = FontFamily.Monospace)

            DividerLine(cybergreen)

            // Source Code
            SectionTitle(stringResource(R.string.source_code), cybergreen)
            ClickableLink(
                label = stringResource(R.string.github_repository),
                url = "https://github.com/PersonX-46/CryptX",
                color = cybergreen,
                icon = Icons.Default.Code
            )

            DividerLine(cybergreen)

            // Contact
            SectionTitle(stringResource(R.string.contact), cybergreen)
            ClickableLink(
                label = "thelonewolf06@proton.me",
                url = "mailto:thelonewolf06@proton.me",
                color = cybergreen,
                icon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(24.dp))
            DividerLine(cybergreen)
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
            color = color,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        )
    }
}

@Composable
fun DividerLine(color: Color) {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        thickness = 1.dp,
        color = color.copy(alpha = 0.4f)
    )
}
@Composable
fun ClickableLink(
    label: String,
    url: String,
    color: Color,
    icon: ImageVector? = null
) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            }
            .padding(vertical = 4.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            color = color,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            textDecoration = TextDecoration.Underline
        )
    }
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