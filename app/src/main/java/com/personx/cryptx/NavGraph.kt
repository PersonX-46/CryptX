package com.personx.cryptx

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
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
import com.personx.cryptx.viewmodel.steganography.SteganographyViewModelRepository

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    windowSizeClass: WindowSizeClass
) {
    NavHost(navController = navController, startDestination = startDestination) {


        composable("home") {
            HomeScreen(windowSizeClass)
        }
        composable("encrypt") {
            EncryptScreen(
                EncryptionViewModelRepository(LocalContext.current),
                windowSizeClass = windowSizeClass
            )
        }
        composable("decrypt") {
            DecryptionScreen(
                DecryptionHistoryRepository(LocalContext.current),
                windowSizeClass = windowSizeClass
            )
        }
        composable("hashGenerator") {
            HashGeneratorScreen(
                windowSizeClass = windowSizeClass
            )
        }
        composable("hashDetector") {
            HashDetector(windowSizeClass = windowSizeClass)
        }
        composable("steganography") {
            SteganographyScreen(
                SteganographyViewModelRepository(LocalContext.current),
                windowSizeClass = windowSizeClass
            )

        }
    }
}
