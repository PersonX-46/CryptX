package com.personx.cryptx

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.personx.cryptx.components.CyberpunkNavBar
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.crypto.SessionKeyManager
import com.personx.cryptx.data.NavBarItem
import com.personx.cryptx.screens.BackupDecisionScreen
import com.personx.cryptx.screens.pinlogin.PinLoginScreen
import com.personx.cryptx.screens.pinsetup.PinSetupScreen
import com.personx.cryptx.ui.theme.CryptXTheme
import com.personx.cryptx.viewmodel.SettingsViewModel
import java.io.File
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settingsViewModel = SettingsViewModel(
            pinCryptoManager = PinCryptoManager(this),
            application = this.applicationContext as Application
        )
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val context = LocalContext.current.applicationContext
            val lifecycleOwner = LocalLifecycleOwner.current
            val currentScreen = remember { mutableStateOf("loading") }


            val navController = rememberNavController()

            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        val prefs = context.getSharedPreferences("secure_prefs", MODE_PRIVATE)
                        val hasSetup = prefs.getString("salt", null) != null &&
                                prefs.getString("iv", null) != null &&
                                prefs.getString("encryptedSessionKey", null) != null


                        currentScreen.value = when {
                            !hasSetup -> "backupDecision"
                            SessionKeyManager.isSessionActive() -> "home"
                            else -> "login"
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            CryptXTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(WindowInsets.navigationBars.asPaddingValues()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen.value) {
                        "loading" -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }

                        "backupDecision" -> BackupDecisionScreen(
                            viewModel = settingsViewModel,
                            windowSizeClass = windowSizeClass,
                            onSkip = {
                                currentScreen.value = "pinSetup"
                            },
                            onRestoreDone = {
                                currentScreen.value = "login"
                            }
                        )

                        "pinSetup" -> PinSetupScreen(
                            pinCryptoManager = PinCryptoManager(context),
                            windowSizeClass = windowSizeClass,
                            onSetupComplete = {
                                currentScreen.value = "login"
                            }
                        )

                        "login" -> PinLoginScreen(
                            pinCryptoManager = PinCryptoManager(context),
                            windowSizeClass = windowSizeClass,
                            onLoginSuccess = { pin ->
                                PinCryptoManager(context).loadSessionKeyIfPinValid(pin)
                                currentScreen.value = "home"
                            }
                        )

                        "home" -> AppContent(windowSizeClass)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        SessionKeyManager.clearSessionKey()
    }
}

@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun restartApp(context: Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExact(
        AlarmManager.RTC,
        System.currentTimeMillis() + 100,
        pendingIntent
    )

    // Kill current process
    exitProcess(0)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppContent(windowSizeClass: WindowSizeClass) {
    val screen = remember { mutableStateOf("home") }
    val navController = rememberNavController()

    LaunchedEffect(screen.value) {
        navController.navigate(screen.value) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    val navItems = listOf(
        NavBarItem(Icons.Filled.Home, "home") {
            navController.navigate("home") {
                launchSingleTop = true
                restoreState = true
            }
        },
        NavBarItem(Icons.Filled.Lock, "encrypt") {
            navController.navigate("encrypt") {
                popUpTo(0) { inclusive = true }
            }
        },
        NavBarItem(Icons.Filled.LockOpen, "decrypt") {
            navController.navigate("decrypt") {
                popUpTo(0) { inclusive = true }
            }
        },
        NavBarItem(Icons.Filled.Code, "hashGenerator") {
            navController.navigate("hashGenerator") {
                popUpTo(0) { inclusive = true }
            }
        },
        NavBarItem(Icons.Filled.Search, "hashDetector") {
            navController.navigate("hashDetector") {
            }
        },
        NavBarItem(Icons.Filled.VisibilityOff, "steganography") {
            navController.navigate("steganography") {
            }
        },
        NavBarItem(Icons.Filled.Settings, "settings") {
            navController.navigate("settings") {
            }
        },

    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomNavBar = currentRoute?.contains("pin")?.not() ?: false

    val bottomNavBarHeight = 80.dp

    Box(modifier = Modifier.fillMaxSize()) {

        // Main AppNavGraph with bottom padding
        Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (showBottomNavBar) bottomNavBarHeight else 0.dp)) {
            CompositionLocalProvider(LocalNavController provides navController) {
                AppNavGraph(
                    navController = navController,
                    windowSizeClass = windowSizeClass,
                    startDestination = screen.value,
                )
            }
        }


        // Cyberpunk navbar floating at bottom
        if (showBottomNavBar) {
            CyberpunkNavBar(
                items = navItems,
                selectedLabel = currentRoute,
                windowSizeClass = windowSizeClass,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CryptXTheme(darkTheme = true) {

    }
}
