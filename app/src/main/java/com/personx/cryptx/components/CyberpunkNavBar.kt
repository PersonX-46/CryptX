package com.personx.cryptx.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.personx.cryptx.data.NavBarItem

@Composable
fun CyberpunkNavBar(
    items: List<NavBarItem>,
    selectedLabel: String,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    // Responsive values
    val height = if (isCompact) 56.dp else 64.dp
    val cornerRadius = if (isCompact) 24.dp else 30.dp
    val horizontalPadding = if (isCompact) 12.dp else 16.dp
    val iconSize = if (isCompact) 20.dp else 24.dp
    val buttonSize = if (isCompact) 36.dp else 40.dp
    val borderWidth = if (isCompact) 0.8.dp else 1.dp

    val selectedItem = items.find { it.label.equals(selectedLabel, ignoreCase = true)}
        ?: items[0]
    Row(
        modifier = modifier
            .height(height)
            .background(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = borderWidth,
                color = cyberpunkGreen.copy(alpha = 0.5f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            val tint by animateColorAsState(
                targetValue = if (isSelected) cyberpunkGreen else cyberpunkGreen.copy(alpha = 0.5f),
                animationSpec = tween(durationMillis = 200),
                label = "iconTint"
            )

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        item.onclick()
                    }
                    .animateContentSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            item.onclick()
                        },
                        modifier = Modifier.size(buttonSize)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(iconSize)
                        )
                    }

                    // Optional label for non-compact screens
                    if (!isCompact) {
                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = cyberpunkGreen,
                                    fontFamily = FontFamily.Monospace
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
