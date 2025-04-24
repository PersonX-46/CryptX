package com.personx.cryptx

import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.components.FloatingNavBar
import com.personx.cryptx.components.Header
import com.personx.cryptx.data.FeatureItem
import com.personx.cryptx.data.NavBarItem
import com.personx.cryptx.ui.theme.CryptXTheme


val featuredItem = listOf(
    FeatureItem(
        Icons.Default.Lock, "Encrypt",
        onClick = { }
    ),
    FeatureItem(
        Icons.Filled.LockOpen, "Decrypt",
        onClick = {}
    ),
    FeatureItem(
        Icons.Default.Code, "Hash Generator",
        onClick = {}
    ),
    FeatureItem(
        Icons.Default.Search, "Hash Detector",
        onClick = {}
    ),
    FeatureItem(
        Icons.Default.VisibilityOff, "Steganography",
        onClick = { }
    ),
    FeatureItem(
        Icons.Default.MoreHoriz, "More",
        onClick = { }
    )
)
class MainActivity : androidx.activity.ComponentActivity() {

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
                    HomeScreen(featuredItem = featuredItem)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(featuredItem: List<FeatureItem> = listOf()){

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
                        onClick = item.onClick
                    )
                }
            }
        }

        FloatingNavBar(
            items = navItems,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) { selectedItem ->
            // Handle navigation item click
            when (selectedItem) {
                else -> {
                    // Handle navigation item click
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CryptXTheme(darkTheme = true) {
        HomeScreen(featuredItem = featuredItem)
    }
}
