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
import androidx.compose.foundation.layout.fillMaxWidth
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
                    "encrypt" -> "encrypt"
                    "decrypt" -> "decrypt"
                    "hashGenerator" -> "hashGenerator"
                    "hashDetector" -> "hashDetector"
                    "steganography" -> "steganography"
                    else -> "H"
                }
                when (selectedScreen.value) {
                    "encrypt" -> subtitle.value = "ENCRYPTION"
                    "decrypt" -> subtitle.value = "DECRYPTION"
                    "hashGenerator" -> subtitle.value = "HASH GENERATOR"
                    "hashDetector" -> subtitle.value = "HASH DETECTOR"
                    "steganography" -> subtitle.value = "STEGANOGRAPHY"
                    else -> subtitle.value = "Invalid screen"
                }
                val navItems = listOf(
                    NavBarItem(
                        Icons.Filled.Home,
                        "Home",
                        onclick = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    ),
                    NavBarItem(
                        Icons.Filled.Lock,
                        "Encrypt",
                        onclick = { selectedScreen.value = "encrypt" }
                    ),
                    NavBarItem(
                        Icons.Filled.LockOpen,
                        "Decrypt",
                        onclick = { selectedScreen.value = "decrypt" }
                    ),
                    NavBarItem(
                        Icons.Filled.Code,
                        "HashGenerator",
                        onclick = { selectedScreen.value = "hashGenerator" }
                    ),
                    NavBarItem(
                        Icons.Filled.Search,
                        "HashDetector",
                        onclick = { selectedScreen.value = "hashDetector" }
                    ),
                    NavBarItem(
                        Icons.Filled.VisibilityOff,
                        "Steganography",
                        onclick = { selectedScreen.value = "steganography" }
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
                                .fillMaxWidth()
                                .padding(bottom = 70.dp)
                        )  {
                            Header(subtitle.value)
                            Spacer(modifier = Modifier.height(30.dp))
                            when (selectedScreen.value) {
                                "encrypt" -> EncryptScreen()
                                "decrypt" -> DecryptionScreen()
                                "hashGenerator" -> HashGeneratorScreen()
                                "hashDetector" -> HashDetector()
                                "steganography" -> SteganographyScreen()
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