package com.personx.cryptx.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personx.cryptx.utils.CryptoUtils.encodeByteArrayToString
import com.personx.cryptx.utils.CryptoUtils.generateRandomIV

@Composable
fun ModePadding(
    onModeSelected: (String) -> Unit,
    onKeySelected: (String) -> Unit,
    transformationList: List<String>,
    keyList: List<String>,
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MaterialDropdownMenu(
                modifier = Modifier.weight(2f),
                items = transformationList,
                onItemSelected = onModeSelected,
                label = "Modes",
            )
            MaterialDropdownMenu(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(),
                items = keyList,
                onItemSelected = onKeySelected,
                label = "Key",
            )
        }
    }
}


@Composable
fun CryptographicTextBox(
    onKeyGenerateClicked: () -> Unit,
    onTranformationSelected: (String) -> Unit,
    onKeySelected: (String) -> Unit,
    transformationList: List<String>,
    keyList: List<String>,
    placeholder1: String,
    placeholder2: String,
    enableTextInput: Boolean,
    text1: String,
    text2: String,
    onText1Change: (String) -> Unit,
    onText2Change: (String) -> Unit,
    keyText: String,
    onKeyTextChange: (String) -> Unit,
    ivText: String,
    enableIV: Boolean,
    onIvTextChange: (String) -> Unit,
    checkSwitch: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
){
    val clipboardManager = LocalClipboardManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp)
    ) {
        ModePadding(
            transformationList = transformationList,
            keyList = keyList,
            onModeSelected = onTranformationSelected,
            onKeySelected = onKeySelected
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.onPrimary.copy(0.1f),
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
                        onText1Change("")
                    },
                text = "Clear Text"
            )

            TransparentEditText(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp),
                maxLines = 5,
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
                maxLines = 5,
                placeholder = placeholder2,
                text = text2,
                onTextChange = onText2Change
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
                    onCheckedChange = onSwitchChange,
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
                    modifier = Modifier
                        .clickable { clipboardManager.setText(AnnotatedString(text2)) },
                    text = "Copy Cipher"
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        color = if (enableIV) MaterialTheme.colorScheme.onSurface else Color.Gray,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "IV",
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable { if (enableIV) clipboardManager.setText(AnnotatedString(ivText)) }
                            .size(20.dp),
                        tint = if (enableIV) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )

                    TransparentEditText(
                        modifier = Modifier.weight(1f), // Allocate proportional space
                        text = ivText,
                        enabled = enableIV,
                        onTextChange = onIvTextChange,
                        placeholder = "IV will appear here"
                    )

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Generate Key",
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable {
                                if (enableIV) {
                                    val iv = generateRandomIV(16)
                                    onIvTextChange(encodeByteArrayToString(iv).trim())
                                } else {
                                    onIvTextChange("")
                                }

                            }
                            .size(20.dp),
                        tint = if (enableIV) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }
            }
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
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Key",
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable { clipboardManager.setText(AnnotatedString(keyText)) }
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )

                    TransparentEditText(
                        modifier = Modifier.weight(1f), // Allocate proportional space
                        text = keyText,
                        enabled = true,
                        onTextChange = onKeyTextChange,
                        placeholder = "Key will appear here"
                    )

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Generate Key",
                        modifier = Modifier
                            .padding(7.dp)
                            .clickable {
                                onKeyGenerateClicked()
                            }
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(height = 10.dp, width = 0.dp))
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)

                .padding(10.dp),
            onClick = onSubmit,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Encrypt",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Encrypt",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview
@Composable
fun CryptographicTextBoxPreview() {

//    CryptXTheme(darkTheme = true) {
//        CryptographicTextBox(
//            placeholder1 = "Enter text to encrypt",
//            placeholder2 = "Encrypted text will appear here",
//            enableTextInput = true,
//            text1 = "",
//            text2 = "",
//            onText1Change = {},
//            onSwtichChange = {},
//            checkSwitch = false,
//            keyText = "",
//            generateKey = {},
//            onKeyTextChange = { },
//            onSubmit = {},
//            onItemSelected = {  },
//            modeList = string,
//            paddingList = TODO(),
//            keyList = TODO()
//        )
//    }

}