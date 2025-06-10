package com.personx.cryptx.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.data.FeatureItem

@Composable
fun HomeScreen(windowSizeClass: WindowSizeClass) {
    val context = LocalContext.current
    val navController = LocalNavController.current

    BackHandler(enabled = true) {}

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val columns = if (isCompact) 2 else 3
    val cardSize = if (isCompact) 120.dp else 140.dp
    val iconSize = if (isCompact) 32.dp else 40.dp
    val cornerSize = if (isCompact) 20.dp else 28.dp
    val padding = if (isCompact) 16.dp else 24.dp
    val spacing = if (isCompact) 16.dp else 24.dp
    val bottomPadding = if (isCompact) 70.dp else 80.dp

    val glowAnimation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            glowAnimation.animateTo(1f, animationSpec = tween(1000))
            glowAnimation.animateTo(0.3f, animationSpec = tween(1000))
        }
    }

    val featuredItems = listOf(
        FeatureItem(Icons.Default.Lock, "Encrypt") { navController.navigate("encrypt") },
        FeatureItem(Icons.Filled.LockOpen, "Decrypt") { navController.navigate("decrypt") },
        FeatureItem(Icons.Default.Code, "Hash Generator") { navController.navigate("hashGenerator") },
        FeatureItem(Icons.Default.Search, "Hash Detector") { navController.navigate("hashDetector") },
        FeatureItem(Icons.Default.VisibilityOff, "Steganography") { navController.navigate("steganography") },
        FeatureItem(Icons.Default.MoreHoriz, "Coming Soon") {
            Toast.makeText(context, "Feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = bottomPadding),
    ) {
        Header("Cryptography Toolkit", windowSizeClass)

        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                        )
                    )
                ),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(spacing))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center,
            ) {
                featuredItems.forEach { item ->
                    FeatureCardButton(
                        icon = item.icon,
                        label = item.label,
                        onClick = item.onClick,
                        windowSizeClass = windowSizeClass,
                        modifier = Modifier
                            .padding(top = if (isCompact) 24.dp else 5.dp, start = if (isCompact) 10.dp else 5.dp, end = if (isCompact) 10.dp else 5.dp)
                    )
                }
            }
        }
    }
}


// Create this in your theme package
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}