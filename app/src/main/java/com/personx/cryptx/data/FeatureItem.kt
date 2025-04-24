package com.personx.cryptx.data

import androidx.compose.ui.graphics.vector.ImageVector

data class FeatureItem (
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)