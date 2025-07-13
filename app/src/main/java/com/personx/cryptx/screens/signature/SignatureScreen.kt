package com.personx.cryptx.screens.signature

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.CyberpunkDropdown
import com.personx.cryptx.components.Header
import com.personx.cryptx.components.SubTitleBar
import com.personx.cryptx.database.encryption.KeyPairHistory
import com.personx.cryptx.screens.ReusableOutputBox
import com.personx.cryptx.viewmodel.signature.SignatureToolViewModel
import com.personx.cryptx.viewmodel.signature.SignatureToolViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignatureToolScreen(
    viewModel: SignatureToolViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val state by viewModel.state.collectAsState()
    val cyberGreen = Color(0xFF00FF9C)
    val darkPanel = Color(0xFF0F1F1C)
    val scope = rememberCoroutineScope()

    val keyPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { viewModel.setKeyFile(uriToFile(context, it)) }
    }

    val targetPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { viewModel.setTargetFile(uriToFile(context, it)) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Header("SIGNATURE TOOL", windowSizeClass = windowSizeClass)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onSurface.copy(0.03f),
                        )
                    )
                )
                .padding(20.dp)
        ) {
            // Mode
            CyberpunkDropdown(
                items = listOf("SIGN", "VERIFY", "GENERATE"),
                selectedItem = state.mode,
                onItemSelected = { viewModel.setMode(it) },
                label = "Select Mode"
            )

            if (state.mode.lowercase() == "verify" || state.mode.lowercase() == "sign") {
                Spacer(Modifier.height(16.dp))

                // Key file
                CyberpunkButton(
                    text = "SELECT ${state.keyLabel.uppercase()} KEY FILE",
                    onClick = { keyPicker.launch(arrayOf("*/*")) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.FileCopy
                )

                state.keyFile?.let {
                    Text(
                        text = "🔐 ${it.name}",
                        color = cyberGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Target file
                CyberpunkButton(
                    text = "SELECT FILE TO ${state.mode.uppercase()}",
                    onClick = { targetPicker.launch(arrayOf("*/*")) },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.AttachFile
                )

                state.targetFile?.let {
                    Text(
                        text = it.name,
                        color = cyberGreen,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Key Preview
                Text("KEY DATASTREAM:", color = cyberGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .border(1.dp, cyberGreen)
                        .background(darkPanel)
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        state.keyPreview.ifBlank { "NO KEY DATA DETECTED" },
                        color = cyberGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Execute signing/verifying
                CyberpunkButton(
                    text = "EXECUTE ${state.mode.uppercase()} SEQUENCE",
                    onClick = { viewModel.startAction() },
                    icon = Icons.Default.FlashOn,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                AnimatedVisibility(visible = state.loading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = cyberGreen,
                        trackColor = darkPanel
                    )
                }

                Spacer(Modifier.height(8.dp))

                AnimatedVisibility(visible = state.resultMessage != null) {
                    Text(
                        ">> ${state.resultMessage!!}",
                        color = if (state.success) cyberGreen else Color(0xFFFF3864),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            if (state.mode.lowercase() == "generate") {
                // --- KEY GENERATOR SECTION ---
                Spacer(Modifier.height(32.dp))
                SubTitleBar(
                    onClick = {
                        //TODO: Implement Session Key Timeout Check
                        viewModel.refreshKeyPairHistory()
                        navController.navigate("keypair_history")
                    },
                    windowSizeClass = windowSizeClass,
                    titleIcon = Icons.Filled.VpnKey,
                    clickableIcon = Icons.Filled.History,
                    title = "KeyPair Generator"
                )
                Spacer(Modifier.height(8.dp))

                Spacer(Modifier.height(16.dp))

                // PRIVATE KEY OUTPUT
                Text("PRIVATE KEY:", color = cyberGreen, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                ReusableOutputBox(
                    content = state.generatedPrivateKey.ifBlank { "NOT GENERATED" },
                    windowSizeClass = windowSizeClass,
                )

                Spacer(Modifier.height(16.dp))

                // PUBLIC KEY OUTPUT
                Text("PUBLIC KEY:", color = cyberGreen, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                ReusableOutputBox(
                    content = state.generatedPublicKey.ifBlank { "NOT GENERATED" },
                    windowSizeClass = windowSizeClass,
                )
                Spacer(Modifier.height(32.dp))
                Row (
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberpunkButton(
                        text = "GENERATE",
                        onClick = { viewModel.generateKeyPair() },
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        icon = Icons.Default.RestartAlt,
                        isActive = !state.loading
                    )
                    CyberpunkButton(
                        text = "SAVE",
                        onClick = {
                            viewModel.saveGeneratedKeyPair()
                            Toast.makeText(context, state.resultMessage, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        icon = Icons.Default.Save,
                        isActive = !state.loading
                    )
                }

            }
        }
    }
}


fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IOException("Unable to open input stream from URI") as Throwable
    val tempFile = File.createTempFile("picked_", null, context.cacheDir)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return tempFile
}
