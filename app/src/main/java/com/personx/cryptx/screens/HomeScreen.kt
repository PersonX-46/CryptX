package com.personx.cryptx.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.personx.cryptx.FeaturedActivity
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.data.FeatureItem

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    BackHandler(enabled = true) {
        // Do nothing — this disables the back action
    }

    remember { mutableStateOf("Home") }
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
            onClick = {
                val intent = Intent(context, FeaturedActivity::class.java).apply {
                    putExtra(FeaturedActivity.EXTRA_SCREEN, "encrypt")
                }
                context.startActivity(intent)
            }
        ),
        FeatureItem(
            icon = Icons.Filled.LockOpen,
            label = "Decrypt",
            onClick = {
                val intent = Intent(context, FeaturedActivity::class.java).apply {
                    putExtra(FeaturedActivity.EXTRA_SCREEN, "decrypt")
                }
                context.startActivity(intent)
            }
        ),
        FeatureItem(
            icon = Icons.Default.Code,
            label = "Hash Generator",
            onClick = {
                val intent = Intent(context, FeaturedActivity::class.java).apply {
                    putExtra(FeaturedActivity.EXTRA_SCREEN, "hashGenerator")
                }
                context.startActivity(intent)
            }
        ),
        FeatureItem(
            icon = Icons.Default.Search,
            label = "Hash Detector",
            onClick = {
                val intent = Intent(context, FeaturedActivity::class.java).apply {
                    putExtra(FeaturedActivity.EXTRA_SCREEN, "hashDetector")
                }
                context.startActivity(intent)
            }
        ),
        FeatureItem(
            icon = Icons.Default.VisibilityOff,
            label = "Steganography",
            onClick = {
                val intent = Intent(context, FeaturedActivity::class.java).apply {
                    putExtra(FeaturedActivity.EXTRA_SCREEN, "steganography")
                }
                context.startActivity(intent)
            }
        ),
        FeatureItem(
            icon = Icons.Default.MoreHoriz,
            label = "Coming Soon",
            onClick = { /* maybe show a toast */ }
        )
    )

    Column {
        Header("SECURITY TOOL")
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
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
        }
    }
}