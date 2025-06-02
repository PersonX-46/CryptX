package com.personx.cryptx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.personx.cryptx.components.CyberpunkNavBar
import com.personx.cryptx.components.Header
import com.personx.cryptx.data.NavBarItem
import com.personx.cryptx.screens.DecryptionScreen
import com.personx.cryptx.screens.EncryptScreen
import com.personx.cryptx.screens.HashDetector
import com.personx.cryptx.screens.HashGeneratorScreen
import com.personx.cryptx.screens.HomeScreen
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
                val screen = intent.getStringExtra(EXTRA_SCREEN) ?: "home"
                val subtitle = remember { mutableStateOf("") }
                val navController = rememberNavController()

                val selectedLabel = remember { mutableStateOf(screen) }

                val navItems = listOf(
                    NavBarItem(Icons.Filled.Home, "Home") {
                        navController.navigate("home")
                        selectedLabel.value = "home"
                    },
                    NavBarItem(Icons.Filled.Lock, "Encrypt") {
                        navController.navigate("encrypt")
                        selectedLabel.value = "encrypt"
                    },
                    NavBarItem(Icons.Filled.LockOpen, "Decrypt") {
                        navController.navigate("decrypt")
                        selectedLabel.value = "decrypt"
                    },
                    NavBarItem(Icons.Filled.Code, "HashGenerator") {
                        navController.navigate("hashGenerator")
                        selectedLabel.value = "hashGenerator"
                    },
                    NavBarItem(Icons.Filled.Search, "HashDetector") {
                        navController.navigate("hashDetector")
                        selectedLabel.value = "hashDetector"
                    },
                    NavBarItem(Icons.Filled.VisibilityOff, "Steganography") {
                        navController.navigate("steganography")
                        selectedLabel.value = "steganography"
                    },
                )

                Surface(modifier = Modifier.fillMaxSize().background(Color.Black)) {
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
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 70.dp)
                        ) {
                            Header(subtitle.value)

                            AppNavGraph(
                                navController = navController,
                                subtitle = subtitle
                            )
                        }

                        CyberpunkNavBar(
                            items = navItems,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 20.dp),
                            selectedLabel = selectedLabel.value
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
                    HomeScreen(
                   )
                }
            }
        )
    }
}