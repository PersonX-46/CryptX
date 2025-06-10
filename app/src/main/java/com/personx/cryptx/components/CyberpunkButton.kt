package com.personx.cryptx.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp


@Composable
fun CyberpunkButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    isActive: Boolean = true,
    isCompact: Boolean = false,
    buttonColor: Color = Color(0xFF00FFAA)
) {
    var isPressed by remember { mutableStateOf(false) }

    // Responsive values
    val buttonHeight = if (isCompact) 40.dp else 48.dp
    val horizontalPadding = if (isCompact) 12.dp else 16.dp
    val iconSize = if (isCompact) 18.dp else 20.dp
    val cornerRadius = if (isCompact) 20.dp else 24.dp

    // Animation values
    val borderWidth by animateDpAsState(
        targetValue = when {
            !isActive -> 0.dp
            isPressed -> 1.5.dp
            else -> 1.dp
        },
        animationSpec = tween(durationMillis = 100),
        label = "borderWidth"
    )

    val alpha by animateFloatAsState(
        targetValue = when {
            !isActive -> 0.5f
            isPressed -> 0.8f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 100),
        label = "buttonAlpha"
    )

    Surface(
        onClick = {
            isPressed = true
            onClick()
        },
        enabled = isActive,
        shape = RoundedCornerShape(cornerRadius),
        color = if (isActive) {
            buttonColor.copy(alpha = 0.1f * alpha)
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        },
        border = BorderStroke(borderWidth, buttonColor.copy(alpha = alpha)),
        modifier = modifier
            .height(buttonHeight)
            .graphicsLayer { this.alpha = alpha },
        interactionSource = remember { MutableInteractionSource() },
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(iconSize),
                tint = if (isActive) buttonColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.width(if (isCompact) 6.dp else 8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    color = if (isActive) buttonColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    shadow = if (isActive && !isPressed) Shadow(
                        color = buttonColor.copy(alpha = 0.5f),
                        offset = Offset(0f, 0f),
                        blurRadius = 8f
                    ) else null
                )
            )
        }
    }
}
