package com.personx.cryptx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.CyberpunkNavBar
import com.personx.cryptx.components.Header
import com.personx.cryptx.data.NavBarItem
import com.personx.cryptx.screens.DecryptionScreen
import com.personx.cryptx.screens.EncryptScreen
import com.personx.cryptx.screens.HashDetector
import com.personx.cryptx.screens.HashGeneratorScreen
import com.personx.cryptx.screens.SteganographyScreen
import com.personx.cryptx.ui.theme.CryptXTheme

class FeaturedActivity : ComponentActivity() {

    companion object {
        const val EXTRA_SCREEN = "decrypt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptXTheme(darkTheme = true) {
                val screen = intent.getStringExtra(EXTRA_SCREEN)
                val selectedScreen = remember { mutableStateOf(screen) }
                val context = LocalContext.current
                val subtitle = remember { mutableStateOf("") }
                val selectedLabel = remember { mutableStateOf("") }
                selectedLabel.value = when (screen) {
                    stringResource(R.string.text_encryption) -> stringResource(R.string.text_encryption)
                    stringResource(R.string.text_decryption) -> stringResource(R.string.text_decryption)
                    stringResource(R.string.hash_generator) -> stringResource(R.string.hash_generator)
                    stringResource(R.string.hash_detector) -> stringResource(R.string.hash_detector)
                    stringResource(R.string.file_steganography) -> stringResource(R.string.file_steganography)
                    else -> stringResource(R.string.home)
                }
                when (selectedScreen.value) {
                    stringResource(R.string.text_encryption) -> subtitle.value = stringResource(R.string.text_encryption)
                    stringResource(R.string.text_decryption) -> subtitle.value = stringResource(R.string.text_decryption)
                    stringResource(R.string.hash_generator) -> subtitle.value = stringResource(R.string.hash_generator)
                    stringResource(R.string.hash_detector)  -> subtitle.value = stringResource(R.string.hash_detector)
                    stringResource(R.string.file_steganography)  -> subtitle.value = stringResource(R.string.file_steganography)
                    else -> subtitle.value = "Invalid screen"
                }
                val navItems = listOf(
                    NavBarItem(
                        Icons.Filled.Home,
                        stringResource(R.string.home) ,
                        onclick = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    ),
                    NavBarItem(
                        Icons.Filled.Lock,
                        stringResource(R.string.text_encryption) ,
                        onclick = { selectedScreen.value = context.getString(R.string.text_encryption)  }
                    ),
                    NavBarItem(
                        Icons.Filled.LockOpen,
                        stringResource(R.string.text_decryption) ,
                        onclick = { selectedScreen.value = context.getString(R.string.text_decryption) }
                    ),
                    NavBarItem(
                        Icons.Filled.Code,
                        stringResource(R.string.hash_generator) ,
                        onclick = { selectedScreen.value = context.getString(R.string.hash_generator) }
                    ),
                    NavBarItem(
                        Icons.Filled.Search,
                        stringResource(R.string.hash_detector) ,
                        onclick = { selectedScreen.value = context.getString(R.string.hash_detector) }
                    ),
                    NavBarItem(
                        Icons.Filled.VisibilityOff,
                        stringResource(R.string.file_steganography) ,
                        onclick = { selectedScreen.value = context.getString(R.string.file_steganography) }
                    ),
                )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.onSurface.copy(0.05f),
                                        MaterialTheme.colorScheme.onPrimary.copy(0.01F)
                                    )
                                )
                            )
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        )  {
                            Header(subtitle.value)
                            Spacer(modifier = Modifier.height(30.dp))
                            when (selectedScreen.value) {
                                stringResource(R.string.text_encryption)  -> EncryptScreen()
                                stringResource(R.string.text_decryption)  -> DecryptionScreen()
                                stringResource(R.string.hash_generator)  -> HashGeneratorScreen()
                                stringResource(R.string.hash_detector)  -> HashDetector()
                                stringResource(R.string.file_steganography)  -> SteganographyScreen()
                                else -> Text("Invalid screen")
                            }
                        }
                        CyberpunkNavBar(
                            items = navItems,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp),
                            selectedLabel = selectedLabel.value,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewFeaturedActivity() {
    CryptXTheme(darkTheme = true) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Header("HASH GENERATOR")
                    Spacer(modifier = Modifier.height(30.dp))
                    HashGeneratorScreen()
                }
            }
        )
    }
}