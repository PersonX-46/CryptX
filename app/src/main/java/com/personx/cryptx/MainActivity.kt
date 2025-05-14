package com.personx.cryptx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.data.FeatureItem
import com.personx.cryptx.data.NavBarItem
import com.personx.cryptx.screens.DecryptionScreen
import com.personx.cryptx.screens.HashDetector
import com.personx.cryptx.screens.HashGeneratorScreen
import com.personx.cryptx.screens.MostUsedAlgo
import com.personx.cryptx.screens.SteganographyScreen
import com.personx.cryptx.ui.theme.CryptXTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptXTheme() {
                // Changed to background color for better edge-to-edge experience
                Surface(
                    modifier = Modifier.
                        fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val currentScreen = remember { mutableStateOf("Home") }
    val glowAnimation = remember { Animatable(0f) }

    // Start the glowing animation loop
    LaunchedEffect(Unit) {
        glowAnimation.animateTo(1f, animationSpec = tween(1000))
        glowAnimation.animateTo(1f, animationSpec = tween(1000))

    }

    val featuredItem = listOf(
        FeatureItem(
            icon = Icons.Default.Lock,
            label = "Encrypt",
            onClick = { currentScreen.value = "Encrypt" }
        ),
        FeatureItem(
            icon = Icons.Filled.LockOpen,
            label = "Decrypt",
            onClick = { currentScreen.value = "Decrypt" }
        ),
        FeatureItem(
            icon = Icons.Default.Code,
            label = "Hash Generator",
            onClick = { currentScreen.value = "Hash Generator" }
        ),
        FeatureItem(
            icon = Icons.Default.Search,
            label = "Hash Detector",
            onClick = { currentScreen.value = "Hash Detector" }
        ),
        FeatureItem(
            icon = Icons.Default.VisibilityOff,
            label = "Steganography",
            onClick = { currentScreen.value = "Steganography" }
        ),
        FeatureItem(
            icon = Icons.Default.MoreHoriz,
            label = "More Tools",
            onClick = { currentScreen.value = "More" }
        )
    )

    val navItems = listOf(
        NavBarItem(Icons.Filled.Home, "Home", Color(0xFF00FFAA)),
        NavBarItem(Icons.Filled.Search, "Search", Color(0xFF00FFAA)),
        NavBarItem(Icons.Filled.Settings, "Settings", Color(0xFF00FFAA)),
    )

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
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glowing header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF00FFAA).copy(alpha = glowAnimation.value * 0.1f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CRYPTX",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00FFAA),
                        shadow = Shadow(
                            color = Color(0xFF00FFAA).copy(alpha = 0.5f),
                            blurRadius = glowAnimation.value * 20f
                        )
                    ),
                    modifier = Modifier.padding(top = 32.dp)
                )
            }
            when (currentScreen.value) {
                "Home" -> {
                    Text(
                        text = "SECURITY TOOLKIT",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00FFAA).copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(featuredItem.size) { index ->
                            val item = featuredItem[index]
                            FeatureCardButton(
                                icon = item.icon,
                                label = item.label,
                                onClick = item.onClick,
                                cardSize = 140.dp,
                                iconSize = 40.dp,
                                cornerSize = 28.dp,
                                borderWidth = 1.dp
                            )
                        }
                    }
                }
                "Encrypt" -> {
                    MostUsedAlgo()
                }
                "Decrypt" -> {
                    DecryptionScreen()
                }
                "Hash Generator" -> {
                    HashGeneratorScreen()
                }
                "Hash Detector" -> {
                    HashDetector()
                }
                "Steganography" -> {
                    SteganographyScreen()
                }
                "More" -> {
                    MostUsedAlgo()
                }
            }
        }

        // Floating cyberpunk navigation bar
        CyberpunkNavBar(
            items = navItems,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) { selectedItem ->
            currentScreen.value = when (selectedItem.label) {
                "Home" -> "Home"
                "Search" -> "Search"
                else -> currentScreen.value
            }
        }
    }
}



@Composable
fun CyberpunkNavBar(
    items: List<NavBarItem>,
    modifier: Modifier = Modifier,
    onItemSelected: (NavBarItem) -> Unit
) {
    val selectedItem = remember { mutableStateOf(items[0]) }

    Row(
        modifier = modifier
            .height(60.dp)
            .background(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(30.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF00FFAA).copy(alpha = 0.5f),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items.forEach { item ->
            IconButton(
                onClick = {
                    selectedItem.value = item
                    onItemSelected(item)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (selectedItem.value == item) item.color else item.color.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CryptXTheme(darkTheme = true) {
        HomeScreen()
    }
}
