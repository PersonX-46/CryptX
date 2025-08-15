package com.personx.cryptx

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.crypto.SessionKeyManager
import com.personx.cryptx.screens.AboutCryptXScreen
import com.personx.cryptx.screens.HashDetector
import com.personx.cryptx.screens.HashGeneratorScreen
import com.personx.cryptx.screens.HomeScreen
import com.personx.cryptx.screens.SteganographyScreen
import com.personx.cryptx.screens.decryptscreen.DecryptHistoryScreen
import com.personx.cryptx.screens.decryptscreen.DecryptPinHandler
import com.personx.cryptx.screens.decryptscreen.DecryptionScreen
import com.personx.cryptx.screens.decryptscreen.EncryptedHistoryHandler
import com.personx.cryptx.screens.encryptscreen.EncryptHistoryScreen
import com.personx.cryptx.screens.encryptscreen.EncryptMainScreen
import com.personx.cryptx.screens.encryptscreen.EncryptPinHandler
import com.personx.cryptx.screens.fileencryption.VaultScreen
import com.personx.cryptx.screens.pinlogin.PassphraseLoginScreen
import com.personx.cryptx.screens.pinsetup.PassphraseSetupScreen
import com.personx.cryptx.screens.settingsscreen.SettingsScreen
import com.personx.cryptx.screens.signature.KeyPairHistoryScreen
import com.personx.cryptx.screens.signature.SignatureToolScreen
import com.personx.cryptx.viewmodel.SettingsViewModel
import com.personx.cryptx.viewmodel.decryption.DecryptionHistoryRepository
import com.personx.cryptx.viewmodel.decryption.DecryptionViewModel
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModel
import com.personx.cryptx.viewmodel.encryption.EncryptionViewModelRepository
import com.personx.cryptx.viewmodel.fileencryption.VaultRepository
import com.personx.cryptx.viewmodel.fileencryption.VaultViewModel
import com.personx.cryptx.viewmodel.fileencryption.VaultViewModelFactory
import com.personx.cryptx.viewmodel.signature.SignatureToolViewModel
import com.personx.cryptx.viewmodel.signature.SignatureToolViewModelFactory
import com.personx.cryptx.viewmodel.steganography.SteganographyViewModelRepository

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun AppNavGraph(
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
    val settingsViewModel = SettingsViewModel(
        pinCryptoManager = PinCryptoManager(context),
        application = context.applicationContext as Application
    )
    val signatureViewModel: SignatureToolViewModel = viewModel(
        factory = SignatureToolViewModelFactory(context.applicationContext as Application)
    )
    val vaultViewModel: VaultViewModel = viewModel(
        factory = VaultViewModelFactory(
            VaultRepository(context)))

    NavHost(navController = navController, startDestination = startDestination) {
        composable("pin_setup") {
            PassphraseSetupScreen(
                pinCryptoManager = PinCryptoManager(context),
                windowSizeClass = windowSizeClass,
                onSetupComplete = {
                    navController.navigate("pin_login") {
                        popUpTo("pin_setup") { inclusive = true } // clears entire backstack
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("pin_login") {
            PassphraseLoginScreen(
                passphraseCryptoManager = PinCryptoManager(context),
                windowSizeClass = windowSizeClass,
                onLoginSuccess = { pin ->
                    PinCryptoManager(context).loadSessionKeyIfPinValid(pin)
                    navController.navigate("home") {
                        popUpTo("pin_login") { inclusive = true } // clears entire backstack
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                SettingsViewModel(
                    PinCryptoManager(context),
                    application = context.applicationContext as Application
                ),
                encryptedViewModel = encryptViewmodel,
                decryptionViewModel = decryptViewModel,
                windowSizeClass = windowSizeClass
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
        composable("settings"){
            SettingsScreen(
                settingsViewModel,
                windowSizeClass,
                navController
            )
        }
        composable("about") {
            AboutCryptXScreen(windowSizeClass)
        }
        composable("signature") {
            SignatureToolScreen(
                windowSizeClass = windowSizeClass,
                navController = navController,
                viewModel = signatureViewModel
            )
        }
        composable("keypair_history") {
            KeyPairHistoryScreen(
                viewModel = signatureViewModel,
                windowSizeClass = windowSizeClass,
                navController = navController
            )
        }

        composable("file_vault") {
            VaultScreen(
                viewModel = vaultViewModel,
                windowSizeClass = windowSizeClass,
                onFileClick = {},
            )
        }
    }
}
