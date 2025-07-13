package com.personx.cryptx.screens.signature

import android.widget.Toast
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.personx.cryptx.database.encryption.KeyPairHistory
import com.personx.cryptx.screens.KeyPairHistoryScreen
import com.personx.cryptx.viewmodel.signature.SignatureToolViewModel
import kotlinx.coroutines.launch

@Composable
fun KeyPairHistoryScreen(
    viewModel: SignatureToolViewModel,
    windowSizeClass: WindowSizeClass,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyPairHistory by viewModel.keyPairHistoryList.collectAsState()


    KeyPairHistoryScreen(
        history = keyPairHistory,
        onItemClick = { item ->
            // Could be used to preview public key or copy to clipboard
        },
        onEditClick = { item ->
            // Optional: You could allow editing name/label if needed
        },
        onDeleteClick = { item ->
            scope.launch {
                val success = viewModel.deleteKeyPair(item)
                if (success) {
                    viewModel.refreshKeyPairHistory()
                    Toast.makeText(context, "Key pair deleted!", Toast.LENGTH_SHORT).show()
                }
            }
        },
        windowSizeClass = windowSizeClass
    )
}
