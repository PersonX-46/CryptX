package com.personx.cryptx.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FeatureCardButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    windowSizeClass: WindowSizeClass,
    isActive: Boolean = true
) {
    val cyberpunkGreen = Color(0xFF00FFAA)
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    // Responsive values
    val cardSize = if (isCompact) 120.dp else 140.dp
    val iconSize = if (isCompact) 36.dp else 48.dp
    val cornerSize = if (isCompact) 16.dp else 20.dp
    val borderWidth = if (isCompact) 0.8.dp else 0.1.dp
    val padding = if (isCompact) 12.dp else 16.dp
    val textSize = if (isCompact) 12.sp else 14.sp

    // Animation states
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "buttonScale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isActive) cyberpunkGreen else cyberpunkGreen.copy(alpha = 0.1f),
        label = "borderColor"
    )
    val iconColor by animateColorAsState(
        targetValue = if (isActive) cyberpunkGreen else cyberpunkGreen.copy(alpha = 0.3f),
        label = "iconColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        label = "textColor"
    )

    Card(
        modifier = modifier
            .size(cardSize)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        if(tryAwaitRelease()) {
                            isPressed = false
                            onClick()
                        } else {
                            isPressed = false
                        }
                    },
                )
            },
        shape = RoundedCornerShape(cornerSize),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.05f)
        ),
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPressed) 4.dp else 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(iconSize),
                tint = iconColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = textSize,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
