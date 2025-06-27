package com.personx.cryptx

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

// Global CompositionLocal for accessing NavHostController in any Composable
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided — did you forget CompositionLocalProvider?")
}
