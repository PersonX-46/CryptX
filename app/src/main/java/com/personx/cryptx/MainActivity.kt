package com.personx.cryptx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.data.FeatureItem
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
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptXTheme() {
                // Changed to background color for better edge-to-edge experience
                Surface(
                    modifier = Modifier.
                        fillMaxSize(),
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(androidx.compose.foundation.layout.WindowInsets.statusBars.asPaddingValues())
                .height(130.dp)
                .padding(16.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cryptx_logo_no_border),
                    contentDescription = "Logo",
                    contentScale = ContentScale.FillHeight,

                )
                VerticalDivider(
                    modifier = Modifier
                        .height(80.dp)
                        .padding(horizontal = 16.dp),
                    thickness = 3.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CryptX",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
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
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CryptXTheme(darkTheme = true) {
        HomeScreen(featuredItem = featuredItem)
    }
}
