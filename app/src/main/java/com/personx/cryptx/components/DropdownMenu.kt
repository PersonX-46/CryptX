package com.personx.cryptx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CyberpunkDropdown(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val expanded = remember { mutableStateOf(false) }
    val cyberpunkGreen = Color(0xFF00FFAA)
    val labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    // Responsive values
    val padding = if (isCompact) 12.dp else 16.dp
    val verticalPadding = if (isCompact) 10.dp else 12.dp
    val cornerRadius = if (isCompact) 6.dp else 8.dp
    val borderWidth = if (isCompact) 0.2.dp else 1.dp

    Box(modifier = modifier) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = labelColor,
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded.value = true }
                    .background(
                        color = cyberpunkGreen.copy(0.05f),
                        shape = RoundedCornerShape(cornerRadius)
                    )

                    .padding(horizontal = padding, vertical = verticalPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedItem,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = if (isCompact) 14.sp else 16.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = if (expanded.value) Icons.Filled.ArrowDropUp
                        else Icons.Filled.ArrowDropDown,
                        contentDescription = if (expanded.value) "Collapse menu" else "Expand menu",
                        tint = cyberpunkGreen,
                        modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .fillMaxWidth(fraction = if (isCompact) 0.95f else 0.85f)
                .background(
                    Color.Black,
                    shape = RoundedCornerShape(cornerRadius)
                )
                .border(borderWidth, cyberpunkGreen, RoundedCornerShape(cornerRadius))
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = if (isCompact) 14.sp else 16.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded.value = false
                    },
                    modifier = Modifier.background(
                        if (item == selectedItem) {
                            cyberpunkGreen.copy(alpha = 0.1f)
                        } else {
                            Color.Transparent
                        }
                    )
                )
            }
        }
    }
}