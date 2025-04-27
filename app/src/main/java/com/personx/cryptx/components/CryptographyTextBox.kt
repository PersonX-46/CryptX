package com.personx.cryptx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.R

@Composable
fun AESComponent(
    onItemSelected: (String) -> Unit,
){
    val modeList = stringArrayResource(R.array.mode_list).toList()
    val paddingList = stringArrayResource(R.array.padding_list).toList()
    val keyList = stringArrayResource(R.array.key_list).toList()

    val dropdownItems = modeList.flatMap { mode ->
        paddingList.map { padding ->
                "$mode/$padding"
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MaterialDropdownMenu(
            modifier = Modifier.weight(2f),
            items = dropdownItems,
            onItemSelected = onItemSelected,
            label = "Modes",
        )
        MaterialDropdownMenu(
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(),
            items = keyList,
            onItemSelected = onItemSelected,
            label = "Key",
        )
    }

}


@Composable
fun CryptographicTextBox(
    placeholder1: String,
    placeholder2: String,
    enableTextInput: Boolean,
    text1: String,
    text2: String,
    onText1Change: (String) -> Unit,
    checkSwitch: Boolean,
    onSwtichChange: (Boolean) -> Unit
){
    val encryptText = remember {
        mutableStateOf("")
    }
    val keyText = remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(18.dp)
                )
                .border(
                    1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(18.dp)
                ),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Badge(
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp)
                    .clickable {
                        encryptText.value = ""
                    },
                text = "Clear Text"
            )

            TransparentEditText(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp),
                enabled = enableTextInput,
                onTextChange = onText1Change,
                placeholder = placeholder1,
                text = text1
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            TransparentEditText(
                modifier = Modifier
                    .padding(10.dp),
                enabled = false,
                placeholder = placeholder2,
                text = text2,
                onTextChange = { }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 5.dp)
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    colors = SwitchColors(
                        checkedThumbColor = MaterialTheme.colorScheme.onSurface,
                        uncheckedThumbColor = Color.Gray,
                        checkedTrackColor = MaterialTheme.colorScheme.onPrimary,
                        uncheckedTrackColor = Color.Transparent,
                        checkedBorderColor = MaterialTheme.colorScheme.onSurface,
                        checkedIconColor = MaterialTheme.colorScheme.onPrimary,
                        uncheckedBorderColor = Color.Gray,
                        uncheckedIconColor = Color.Gray,
                        disabledCheckedThumbColor = Color.Gray,
                        disabledCheckedTrackColor = Color.Gray,
                        disabledCheckedBorderColor = Color.Gray,
                        disabledCheckedIconColor = Color.Gray,
                        disabledUncheckedThumbColor = Color.Gray,
                        disabledUncheckedTrackColor = Color.Gray,
                        disabledUncheckedBorderColor = Color.Gray,
                        disabledUncheckedIconColor = Color.Gray
                    ),
                    checked = checkSwitch,
                    onCheckedChange = onSwtichChange,
                    thumbContent = {
                        Icon(
                            modifier = Modifier
                                .size(14.dp),
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Switch Icon",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                )
                Badge(
                    text = "Copy Cipher"
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onPrimary,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Key",
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable { /* Handle add key action */ }
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    TransparentEditText(
                        modifier = Modifier.weight(1f), // Allocate proportional space
                        text = keyText.value,
                        enabled = true,
                        onTextChange = { keyText.value = it },
                        placeholder = "Key will appear here"
                    )

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Generate Key",
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable { /* Handle add key action */ }
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

        }

    }

}

@Preview
@Composable
fun CryptographicTextBoxPreview() {

    CryptographicTextBox(
        placeholder1 = "Enter text to encrypt",
        placeholder2 = "Encrypted text will appear here",
        enableTextInput = true,
        text1 = "",
        text2 = "",
        onText1Change = {},
        onSwtichChange = {},
        checkSwitch = false
    )
}