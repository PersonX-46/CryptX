package com.personx.cryptx.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.personx.cryptx.crypto.SessionKeyManager

@Composable
fun SubTitleBar(
    onClick: () -> Unit,
    windowSizeClass: WindowSizeClass,
    titleIcon: ImageVector,
    clickableIcon: ImageVector,
    title: String,
) {

    val cyberpunkGreen = MaterialTheme.colorScheme.onSurface
    // Responsive values
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
            .border(0.5.dp, cyberpunkGreen, shape = RoundedCornerShape(15.dp))
            .padding(horizontal = 5.dp),
    ) {
        Icon(
            imageVector = titleIcon,
            contentDescription = "Title Icon",
            tint = cyberpunkGreen,
            modifier = Modifier.size(if (isCompact) 24.dp else 28.dp)
        )
        Text(
            modifier = Modifier.padding(start = 3.dp),
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium.copy(
                color = cyberpunkGreen.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
                fontSize = if (isCompact) MaterialTheme.typography.labelLarge.fontSize
                else MaterialTheme.typography.titleSmall.fontSize
            )
        )
        IconButton(
            onClick = {onClick}
        ) {
            Icon(
                imageVector = clickableIcon,
                contentDescription = "History",
                tint = cyberpunkGreen,
                modifier = Modifier.size(if (isCompact) 24.dp else 28.dp)
            )
        }
    }
}