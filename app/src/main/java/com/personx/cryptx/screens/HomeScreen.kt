package com.personx.cryptx.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.personx.cryptx.LocalNavController
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.data.FeatureItem
import com.personx.cryptx.viewmodel.SettingsViewModel

@Composable
fun HomeScreen(
    viewModel: SettingsViewModel = viewModel(),
    windowSizeClass: WindowSizeClass
) {

    val context = LocalContext.current
    val navController = LocalNavController.current
    val state = viewModel.state.collectAsState()

    // Cyberpunk colors
    val cyberGreen = Color(0xFF00FF9D)

    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 16.dp else 24.dp
    val spacing = if (isCompact) 16.dp else 24.dp
    val bottomPadding = if (isCompact) 70.dp else 80.dp

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

        Column(
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

            // Featured Items Grid
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
                            .padding(top = 5.dp, end = 5.dp)
                    )
                }
            }

            // Custom Change PIN Section
            Column(
                modifier = Modifier
                    .padding(top = spacing * 2)
                    .fillMaxWidth(0.8f)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { viewModel.updateShowPinDialog(true)}
                    .padding(spacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Change PIN",
                    tint = cyberGreen,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Settings",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace
                    )
                )

                Text(
                    "Configure app settings",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // Cyberpunk-styled PIN Change Dialog
    if (state.value.showPinDialog) {
        navController.navigate("settings")
    }
}

