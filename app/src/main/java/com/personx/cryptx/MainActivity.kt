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
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.personx.cryptx.components.CyberpunkNavBar
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.data.NavBarItem
import com.personx.cryptx.screens.pinlogin.PinLoginScreen
import com.personx.cryptx.screens.pinsetup.PinSetupScreen
import com.personx.cryptx.ui.theme.CryptXTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Calculate window size class for responsive design
            val windowSizeClass = calculateWindowSizeClass(this)

            CryptXTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimary),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Changed to background color for better edge-to-edge experience
                    val prefs = getSharedPreferences("secure_prefs", MODE_PRIVATE)
                    val saltString = prefs.getString("salt", null)
                    val ivString = prefs.getString("iv", null)
                    val secretString = prefs.getString("secret", null)

                    // Track which screen to show
                    val currentScreen = remember { mutableStateOf(
                        if (saltString == null || ivString == null || secretString == null)
                            "pinSetup"
                        else
                            "login"
                    )}

                    when (currentScreen.value) {
                        "pinSetup" -> {
                            PinSetupScreen(
                                pinCryptoManager = PinCryptoManager(LocalContext.current),
                                windowSizeClass = windowSizeClass,
                                onSetupComplete = {
                                    currentScreen.value = "login" // Update screen after setup
                                }
                            )
                        }
                        "login" -> {
                            PinLoginScreen(
                                pinCryptoManager = PinCryptoManager(LocalContext.current),
                                windowSizeClass = windowSizeClass,
                                onLoginSuccess = { pin ->
                                    currentScreen.value = "home"
                                }
                            )
                        }
                        "home" -> {
                            AppContent(windowSizeClass)                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppContent(windowSizeClass: WindowSizeClass) {
    val screen = remember { mutableStateOf("home") }
    val navController = rememberNavController()
    val selectedLabel = remember { mutableStateOf(screen.value) }

    LaunchedEffect(screen.value) {
        navController.navigate(screen.value) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    val navItems = listOf(
        NavBarItem(Icons.Filled.Home, "Home") {
            navController.navigate("home") {
                launchSingleTop = true
                restoreState = true
            }
            selectedLabel.value = "home"
        },
        NavBarItem(Icons.Filled.Lock, "Encrypt") {
            navController.navigate("encrypt") {
                popUpTo(0) { inclusive = true }
            }
            selectedLabel.value = "encrypt"
        },
        NavBarItem(Icons.Filled.LockOpen, "Decrypt") {
            navController.navigate("decrypt") {
                popUpTo(0) { inclusive = true }
            }
            selectedLabel.value = "decrypt"
        },
        NavBarItem(Icons.Filled.Code, "Hash") {
            navController.navigate("hashGenerator") {
                popUpTo(0) { inclusive = true }
            }
            selectedLabel.value = "hashGenerator"
        },
        NavBarItem(Icons.Filled.Search, "Detect") {
            navController.navigate("hashDetector") {
                popUpTo(0) { inclusive = true }
            }
            selectedLabel.value = "hashDetector"
        },
        NavBarItem(Icons.Filled.VisibilityOff, "Stego") {
            navController.navigate("steganography") {
                popUpTo(0) { inclusive = true }
            }
            selectedLabel.value = "steganography"
        }
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ) {
            CompositionLocalProvider(LocalNavController provides navController) {
                AppNavGraph(
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                    startDestination = screen.value
                )
            }
        }

        CyberpunkNavBar(
            items = navItems,
            selectedLabel = selectedLabel.value,
            windowSizeClass = windowSizeClass,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CryptXTheme(darkTheme = true) {

    }
}
