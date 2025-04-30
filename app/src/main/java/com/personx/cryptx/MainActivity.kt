package com.personx.cryptx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.components.FloatingNavBar
import com.personx.cryptx.components.Header
import com.personx.cryptx.data.FeatureItem
import com.personx.cryptx.data.NavBarItem
import com.personx.cryptx.screens.DecryptionScreen
import com.personx.cryptx.screens.MostUsedAlgo
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
fun HomeScreen(){


    val currentScreen = remember {
        mutableStateOf("Home")
    }

    val featuredItem = listOf(
        FeatureItem(
            Icons.Default.Lock, "Encrypt",
            onClick = { currentScreen.value = "Encrypt" }
        ),
        FeatureItem(
            Icons.Filled.LockOpen, "Decrypt",
            onClick = { currentScreen.value = "Decrypt" }
        ),
        FeatureItem(
            Icons.Default.Code, "Hash Generator",
            onClick = { currentScreen.value = "Hash Generator" }
        ),
        FeatureItem(
            Icons.Default.Search, "Hash Detector",
            onClick = { currentScreen.value = "Hash Detector" }
        ),
        FeatureItem(
            Icons.Default.VisibilityOff, "Steganography",
            onClick = { currentScreen.value = "Steganography" }
        ),
        FeatureItem(
            Icons.Default.MoreHoriz, "More",
            onClick = { currentScreen.value = "More" }
        )
    )

    val navItems = listOf(
        NavBarItem(Icons.Filled.Home, "Home"),
        NavBarItem(Icons.Filled.Search, "Search"),
        NavBarItem(Icons.Filled.Person, "Profile"),
    )

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Use the correct background color
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header()

            if (currentScreen.value == "Home") {
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
            } else if (currentScreen.value == "Encrypt") {
                MostUsedAlgo()
            } else if (currentScreen.value == "Decrypt") {
                DecryptionScreen()
            } else if (currentScreen.value == "Hash Generator") {
                MostUsedAlgo()
            } else if (currentScreen.value == "Hash Detector") {
                MostUsedAlgo()
            } else if (currentScreen.value == "Steganography") {
                MostUsedAlgo()
            } else if (currentScreen.value == "More") {
                MostUsedAlgo()
            }
        }

        FloatingNavBar(
            items = navItems,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) { selectedItem ->
            // Handle navigation item click
            currentScreen.value = when (selectedItem.label) {
                "Home" -> "Home"
                "Search" -> "Search"
                "Profile" -> "Encrypt"
                else -> currentScreen.value
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
