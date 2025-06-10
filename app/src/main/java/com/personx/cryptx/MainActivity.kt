package com.personx.cryptx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.personx.cryptx.crypto.PinCryptoManager
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
                                onLoginSuccess = {
                                    val intent = Intent(this, FeaturedActivity::class.java)
                                    startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CryptXTheme(darkTheme = true) {

    }
}
