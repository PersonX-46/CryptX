package com.personx.cryptx.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.personx.cryptx.components.FeatureCardButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.crypto.PinCryptoManager
import com.personx.cryptx.data.FeatureItem
import com.personx.cryptx.viewmodel.HashDetectorViewModel
import com.personx.cryptx.viewmodel.HomeScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel(),
    windowSizeClass: WindowSizeClass
) {

    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val state = viewModel.state.collectAsState()

    // State for PIN change dialog


    // Cyberpunk colors
    val cyberGreen = Color(0xFF00FF9D)
    val cyberDark = Color(0xFF0A0A12)
    val cyberTeal = Color(0xFF00E0FF)

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
                        color = cyberDark.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = cyberGreen.copy(alpha = 0.5f),
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
                    "Change PIN",
                    color = cyberGreen,
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    "Update your security PIN",
                    color = cyberTeal,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // Cyberpunk-styled PIN Change Dialog
    if (state.value.showPinDialog) {
        Dialog(
            onDismissRequest = { viewModel.updateShowPinDialog(false)}
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = cyberGreen,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(24.dp)
            ) {
                Text(
                    "CHANGE SECURITY PIN",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = cyberGreen,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CyberpunkPinField(
                    value = state.value.currentPin?: "",
                    onValueChange = { viewModel.updateCurrentPin(it) },
                    label = "Current PIN",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                CyberpunkPinField(
                    value = state.value.newPin?: "",
                    onValueChange = { viewModel.updateNewPin(it) },
                    label = "New PIN",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                CyberpunkPinField(
                    value = state.value.confirmPin?: "",
                    onValueChange = { viewModel.updateConfirmPin(it) },
                    label = "Confirm PIN",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { viewModel.updateShowPinDialog(false) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = cyberTeal
                        )
                    ) {
                        Text("CANCEL")
                    }

                    Spacer(Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val result = PinCryptoManager(
                                context = context
                            ).changePinAndRekeyDatabase(
                                oldPin = "2580",
                                newPin = "1234",
                            )
                            Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show()
//                            viewModel.updatePin(
//                                oldPin = state.value.currentPin ?: "",
//                                newPin = state.value.newPin?: "",
//                                confirmPin = state.value.confirmPin?: "",
//                                onResult = { success ->
//                                    if (success) {
//                                        Toast.makeText(context, "PIN changed successfully!", Toast.LENGTH_SHORT).show()
//                                        viewModel.updateShowPinDialog(false)
//                                    } else {
//                                        Toast.makeText(context, "Failed to change PIN. Please try again.", Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cyberGreen,
                            contentColor = Color.Black
                        ),
                        border = BorderStroke(1.dp, cyberTeal)
                    ) {
                        Text("CONFIRM")
                    }
                }
            }
        }
    }
}

@Composable
fun CyberpunkPinField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val cyberGreen = Color(0xFF00FF9D)
    val cyberDark = Color(0xFF0A0A12)

    Column(modifier = modifier) {
        Text(
            text = label,
            color = cyberGreen.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = cyberDark.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 1.dp,
                    color = cyberGreen.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(12.dp),
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

// Create this in your theme package
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}