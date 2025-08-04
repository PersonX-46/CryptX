package com.personx.cryptx.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SubTitleBar(
    title: String,
    onTitleChange: (String) -> Unit,
    onClick: () -> Unit,
    windowSizeClass: WindowSizeClass,
    titleIcon: ImageVector,
    clickableIcon: ImageVector,
) {
    val cyberpunkGreen = MaterialTheme.colorScheme.onSurface
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, cyberpunkGreen, shape = RoundedCornerShape(15.dp))
            .padding(horizontal = 5.dp),
    ) {
        Icon(
            imageVector = titleIcon,
            contentDescription = "Title Icon",
            tint = cyberpunkGreen,
            modifier = Modifier.size(if (isCompact) 24.dp else 28.dp).padding(5.dp)
        )

        BasicTextField(
            value = title,
            onValueChange = onTitleChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.labelMedium.copy(
                color = cyberpunkGreen.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                fontSize = if (isCompact) MaterialTheme.typography.labelLarge.fontSize
                else MaterialTheme.typography.titleSmall.fontSize
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp, vertical = 8.dp)
        )

        IconButton(
            onClick = onClick
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
