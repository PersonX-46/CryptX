package com.personx.cryptx

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.personx.cryptx.screens.DecryptionScreen
import com.personx.cryptx.screens.EncryptScreen
import com.personx.cryptx.screens.HashDetector
import com.personx.cryptx.screens.HashGeneratorScreen
import com.personx.cryptx.screens.HomeScreen
import com.personx.cryptx.screens.SteganographyScreen
import com.personx.cryptx.viewmodel.decryption.DecryptionHistoryRepository
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModelRepository

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String = "home") {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("home") {
            HomeScreen()
        }
        composable("encrypt") {
            EncryptScreen(EncryptionViewModelRepository(LocalContext.current))
        }
        composable("decrypt") {
            DecryptionScreen(DecryptionHistoryRepository(LocalContext.current))
        }
        composable("hashGenerator") {
            HashGeneratorScreen()
        }
        composable("hashDetector") {
            HashDetector()
        }
        composable("steganography") {
            SteganographyScreen()

        }
    }
}
