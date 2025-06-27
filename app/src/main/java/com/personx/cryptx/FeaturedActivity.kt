package com.personx.cryptx

//import com.personx.cryptx.screens.LocalNavController
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.personx.cryptx.components.CyberpunkNavBar
import com.personx.cryptx.data.NavBarItem
import com.personx.cryptx.ui.theme.CryptXTheme

class FeaturedActivity : ComponentActivity() {
    companion object {
        const val EXTRA_SCREEN = "decrypt"
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptXTheme(darkTheme = true) {

                val windowSizeClass = calculateWindowSizeClass(this)
                val screen = intent.getStringExtra(EXTRA_SCREEN) ?: "home"
                val navController = rememberNavController()
                val selectedLabel = remember { mutableStateOf(screen) }

                // Responsive values
                val navBarPadding = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) 16.dp else 24.dp
                val contentPadding = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) 70.dp else 80.dp

                LaunchedEffect(Unit) {
                    navController.navigate(screen) {
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
                            launchSingleTop = true
                            restoreState = true
                        }
                        selectedLabel.value = "encrypt"
                    },
                    NavBarItem(Icons.Filled.LockOpen, "Decrypt") {
                        navController.navigate("decrypt") {
                            launchSingleTop = true
                            restoreState = true
                        }
                        selectedLabel.value = "decrypt"
                    },
                    NavBarItem(Icons.Filled.Code, "Hash") {
                        navController.navigate("hashGenerator") {
                            launchSingleTop = true
                            restoreState = true
                        }
                        selectedLabel.value = "hashGenerator"
                    },
                    NavBarItem(Icons.Filled.Search, "Detect") {
                        navController.navigate("hashDetector") {
                            launchSingleTop = true
                            restoreState = true
                        }
                        selectedLabel.value = "hashDetector"
                    },
                    NavBarItem(Icons.Filled.VisibilityOff, "Stego") {
                        navController.navigate("steganography") {
                            launchSingleTop = true
                            restoreState = true
                        }
                        selectedLabel.value = "steganography"
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                                .padding(bottom = contentPadding)
                        ) {
                            CompositionLocalProvider(LocalNavController provides navController) {
                                AppNavGraph(
                                    navController = navController,
                                    windowSizeClass = windowSizeClass,
                                    startDestination = screen
                                )
                            }
                        }

                        CyberpunkNavBar(
                            items = navItems,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = navBarPadding),
                            selectedLabel = selectedLabel.value,
                            windowSizeClass = windowSizeClass
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

                }
            }
        )
    }
}