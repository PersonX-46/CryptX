package com.personx.cryptx.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personx.cryptx.PrefsHelper
import com.personx.cryptx.database.encryption.KeyPairHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KeyPairHistoryItem(
    entry: KeyPairHistory,
    cyberpunkGreen: Color,
    onItemClick: (KeyPairHistory) -> Unit,
    onEditClick: (KeyPairHistory) -> Unit,
    onDeleteClick: (KeyPairHistory) -> Unit,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass,
    enableEditing: Boolean = true,
    enableDeleting: Boolean = true
) {
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val padding = if (isCompact) 12.dp else 16.dp
    val verticalSpacing = if (isCompact) 8.dp else 12.dp
    val smallVerticalSpacing = if (isCompact) 4.dp else 8.dp
    val iconSize = if (isCompact) 20.dp else 24.dp
    val smallIconSize = if (isCompact) 14.dp else 16.dp
    val chipPadding = if (isCompact) 4.dp else 8.dp
    val keyPreviewLength = if (isCompact) 16 else 28

    val context = LocalContext.current
    val prefs = PrefsHelper(context)

    Card(
        modifier = modifier
            .clickable { onItemClick(entry) }
            .padding(vertical = if (isCompact) 2.dp else 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, cyberpunkGreen.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(padding)) {

            // Row with timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(cyberpunkGreen.copy(alpha = 0.1f))
                        .padding(horizontal = chipPadding, vertical = chipPadding)
                ) {
                    Text(
                        text = "RSA/PKCS8",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = cyberpunkGreen,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Text(
                    text = SimpleDateFormat(
                        if (isCompact) "MMM dd" else "MMM dd, HH:mm",
                        Locale.getDefault()
                    ).format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(verticalSpacing))

            // Public key section
            HistoryItemSection(
                title = "NAME",
                content = entry.name,
                maxChars = keyPreviewLength,
                titleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                contentColor = cyberpunkGreen.copy(alpha = 0.9f),
                isCompact = isCompact
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

            HistoryItemSection(
                title = "PUBLIC KEY",
                content = entry.publicKey,
                maxChars = keyPreviewLength,
                titleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                contentColor = cyberpunkGreen.copy(alpha = 0.9f),
                isCompact = isCompact
            )

            Spacer(modifier = Modifier.height(smallVerticalSpacing))

            // Private key section
            HistoryItemSection(
                title = "PRIVATE KEY",
                content = if (prefs.hidePlainTextInEncryptedHistory) "************" else entry.privateKey,
                maxChars = keyPreviewLength,
                titleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                isCompact = isCompact
            )

            Spacer(modifier = Modifier.height(verticalSpacing))

            // Footer actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = { onEditClick(entry) },
                    modifier = Modifier.size(iconSize),
                    enabled = enableEditing
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = cyberpunkGreen.copy(alpha = 0.7f),
                        modifier = Modifier.size(smallIconSize)
                    )
                }

                Spacer(modifier = Modifier.width(if (isCompact) 4.dp else 8.dp))

                IconButton(
                    onClick = { onDeleteClick(entry) },
                    modifier = Modifier.size(iconSize),
                    enabled = enableDeleting
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = cyberpunkGreen.copy(alpha = 0.7f),
                        modifier = Modifier.size(smallIconSize)
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItemSection(
    title: String,
    content: String,
    maxChars: Int,
    titleColor: Color,
    contentColor: Color,
    isCompact: Boolean
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = titleColor,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 12.sp else 14.sp
            )
        )
        Text(
            text = if (content.length > maxChars) "${content.take(maxChars)}..." else content,
            style = MaterialTheme.typography.bodySmall.copy(
                color = contentColor,
                fontFamily = FontFamily.Monospace,
                fontSize = if (isCompact) 12.sp else 14.sp
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
