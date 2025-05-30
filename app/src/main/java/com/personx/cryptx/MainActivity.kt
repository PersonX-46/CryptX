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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.screens.HomeScreen
import com.personx.cryptx.screens.pinlogin.PinLoginScreen
import com.personx.cryptx.screens.pinsetup.PinSetupScreen
import com.personx.cryptx.ui.theme.CryptXTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptXTheme(darkTheme = true) {
                // Changed to background color for better edge-to-edge experience
                rememberNavController()
                Surface(
                    modifier = Modifier.
                        fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimary),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PinLoginScreen(pinCryptoManager = PinCryptoManager(LocalContext.current)) {
                        val intent = Intent(this, FeaturedActivity::class.java)
                        startActivity(intent)
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
        PinSetupScreen(pinCryptoManager = PinCryptoManager(LocalContext.current)) {

        }
    }
}
