package com.personx.cryptx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.personx.cryptx.screens.DecryptionScreen
import com.personx.cryptx.screens.EncryptScreen
import androidx.navigation.compose.composable
import com.personx.cryptx.screens.HashDetector
import com.personx.cryptx.screens.HashGeneratorScreen
import com.personx.cryptx.screens.HomeScreen
import com.personx.cryptx.screens.SteganographyScreen
import com.personx.cryptx.viewmodel.encryption.EncryptionHistoryRepository

@Composable
fun AppNavGraph(navController: NavHostController, subtitle: MutableState<String>) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            subtitle.value = "Security Tool"
            HomeScreen()
        }
        composable("encrypt") {
            subtitle.value = "ENCRYPTION"
            EncryptScreen(EncryptionHistoryRepository(LocalContext.current))
        }
        composable("decrypt") {
            subtitle.value = "DECRYPTION"
            DecryptionScreen()
        }
        composable("hashGenerator") {
            subtitle.value = "HASH GENERATOR"
            HashGeneratorScreen()
        }
        composable("hashDetector") {
            subtitle.value = "HASH DETECTOR"
            HashDetector()
        }
        composable("steganography") {
            subtitle.value = "STEGANOGRAPHY"
            SteganographyScreen()
        }
    }
}
