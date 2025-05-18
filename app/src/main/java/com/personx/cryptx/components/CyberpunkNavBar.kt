package com.personx.cryptx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.personx.cryptx.data.NavBarItem

@Composable
fun CyberpunkNavBar(
    items: List<NavBarItem>,
    selectedLabel: String, // <--- new parameter
    modifier: Modifier = Modifier,
) {
    val selectedItem = remember {
        mutableStateOf(items.find { it.label.equals(selectedLabel, ignoreCase = true) } ?: items[0])
    }

    Row(
        modifier = modifier
            .height(60.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(30.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF00FFAA).copy(alpha = 0.5f),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.forEach { item ->
            IconButton(
                onClick = {
                    selectedItem.value = item
                    item.onclick() // <-- make sure to invoke the function
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (selectedItem.value == item) Color(0xFF00FFAA) else Color(0xFF00FFAA).copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
