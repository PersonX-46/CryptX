package com.personx.cryptx.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.AESComponent
import com.personx.cryptx.components.CryptographicTextBox
import com.personx.cryptx.components.MaterialDropdownMenu
import com.personx.cryptx.ui.theme.CryptXTheme

@Composable
fun MostUsedAlgo(){
    val selectedAlgorithm = remember {
        mutableStateOf("AES")
    }
    val mostUsedAlgorithms = listOf(
        "AES",
        "DES",
        "RSA",
        "Blowfish",
        "Twofish",
        "Blowfish",
        "Blowfish",
        "Blowfish",
        "Blowfish",
        "Blowfish",

    )
    val isBase64Enabled = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MaterialDropdownMenu(
            modifier = Modifier
                .padding(bottom = 1.dp)
                .padding(horizontal = 16.dp)
                .wrapContentWidth(),
            items = mostUsedAlgorithms,
            onItemSelected = { selectedAlgorithm.value = it },
            label = "Algorithms"
        )
        Spacer(
            modifier = Modifier
                .size(height = 20.dp, width = 0.dp)
        )
        Spacer(
            modifier = Modifier
                .size(height = 20.dp, width = 0.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selectedAlgorithm.value.equals("AES")) {
                AESComponent(
                    onItemSelected = {},
                )
            }
        }

        CryptographicTextBox(
            placeholder1 = "Enter Text to Encrypt",
            placeholder2 = "The Decrypted Text Will Appear Here",
            enableTextInput = true,
            text1 = "",
            text2 = "",
            onText1Change = {},
            checkSwitch = isBase64Enabled.value,
            onSwtichChange = { isBase64Enabled.value = it }
        )
    }
}

@Composable
fun MostUsedAlgorithmsLayout(
    algorithms: List<String>,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            algorithms.take(3).forEach { algorithm ->
                AlgorithmCard(algorithm)
            }
        }
    }
}

@Composable
fun AlgorithmCard(algorithm: String) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .background(
                MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, color = MaterialTheme.colorScheme.onSurface, shape = RoundedCornerShape(5.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = algorithm,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun PreviewEncrypt() {
    CryptXTheme(darkTheme = true) {
        MostUsedAlgo()
    }
}