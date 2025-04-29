package com.personx.cryptx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransparentEditText(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    maxLines: Int = 1,
    onTextChange: (String) -> Unit,
    placeholder: String = ""
) {
    BasicTextField(
        enabled = enabled,
        maxLines = maxLines,
        value = text, // Use the text parameter directly
        onValueChange = onTextChange, // Update the parent state directly
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Transparent),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(
            MaterialTheme.colorScheme.onSurface
        ), // Set cursor color here

        decorationBox = { innerTextField ->
            if (text.isEmpty()) { // Check the text parameter directly
                androidx.compose.material3.Text(
                    text = placeholder,
                    style = TextStyle(color = Color.Gray, fontSize = 16.sp)
                )
            }
            innerTextField()
        }
    )
}