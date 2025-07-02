package com.personx.cryptx

import android.annotation.SuppressLint
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.screens.decryptscreen.DecryptionScreen
import com.personx.cryptx.screens.encryptscreen.EncryptMainScreen
import com.personx.cryptx.screens.HashDetector
import com.personx.cryptx.screens.HashGeneratorScreen
import com.personx.cryptx.screens.HomeScreen
import com.personx.cryptx.screens.SteganographyScreen
import com.personx.cryptx.screens.decryptscreen.DecryptHistoryScreen
import com.personx.cryptx.screens.decryptscreen.DecryptPinHandler
import com.personx.cryptx.screens.decryptscreen.EncryptedHistoryHandler
import com.personx.cryptx.screens.encryptscreen.EncryptHistoryScreen
import com.personx.cryptx.screens.encryptscreen.EncryptPinHandler
import com.personx.cryptx.viewmodel.HomeScreenViewModel
import com.personx.cryptx.viewmodel.decryption.DecryptionHistoryRepository
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModelRepository
import com.personx.cryptx.viewmodel.steganography.SteganographyViewModelRepository

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavGraph(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String,
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val encryptViewmodel = EncryptionViewModel(
        repository = EncryptionViewModelRepository(
            context
        )
    )
    val decryptViewModel = DecryptionViewModel(
        repository = DecryptionHistoryRepository(context)
    )
    NavHost(modifier = modifier, navController = navController, startDestination = startDestination) {
        composable("home") {
            HomeScreen(
                HomeScreenViewModel(
                    PinCryptoManager(context)
                ),
                windowSizeClass
            )
        }
        composable("encrypt") {
            EncryptMainScreen(
                viewModel = encryptViewmodel,
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }
        composable("encrypt_history") { 
            EncryptHistoryScreen(
                viewModel = encryptViewmodel,
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }
        composable("encrypt_pin_handler") {
            EncryptPinHandler(
                viewModel = encryptViewmodel,
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }
        composable("decrypt") {
            DecryptionScreen(
                viewModel = decryptViewModel,
                windowSizeClass = windowSizeClass,
                navController = navController,
            )
        }
        composable("decrypt_history") {
            DecryptHistoryScreen(
                viewModel = decryptViewModel,
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }
        composable("decrypt_pin_handler") {
            DecryptPinHandler(
                viewModel = decryptViewModel,
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }
        composable("decrypt_encrypted_history_handler") {
            EncryptedHistoryHandler(
                viewModel = decryptViewModel,
                windowSizeClass = windowSizeClass,
                navController = navController
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
                SteganographyViewModelRepository(context),
                windowSizeClass = windowSizeClass
            )
        }
    }
}
