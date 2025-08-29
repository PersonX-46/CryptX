package com.personx.cryptx.screens.signature

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.personx.cryptx.R
import com.personx.cryptx.components.CyberpunkInputBox
import com.personx.cryptx.components.Header
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
    viewModel.refreshKeyPairHistory()

    Column {
        Header(R.string.key_pair_history_header, windowSizeClass)
        Column (
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurface.copy(0.05f),
                            MaterialTheme.colorScheme.onPrimary.copy(0.01f)
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CyberpunkInputBox(
                modifier = Modifier.padding(17.dp),
                value = viewModel.searchQuery.value,
                onValueChange = { query: String ->
                    viewModel.updateSearchQuery(query)
                },
                placeholder = R.string.search_history_with_name,
            )
            KeyPairHistoryScreen(
                history = viewModel.filteredHistory.value,
                onItemClick = { item ->
                    // Could be used to preview public key or copy to clipboard
                    viewModel.setMode("GENERATE")
                    viewModel.updateTitle(item.name)
                    viewModel.setPublicKeyText(item.publicKey)
                    viewModel.setPrivateKeyText(item.privateKey)
                    navController.navigate("signature")
                },
                onEditClick = { item ->
                    // Optional: You could allow editing name/label if needed
                },
                onDeleteClick = { item ->
                    scope.launch {
                        val success = viewModel.deleteKeyPair(item)
                        viewModel.refreshKeyPairHistory()
                        navController.navigate("keypair_history")
                        Toast.makeText(context, "Key pair deleted!", Toast.LENGTH_SHORT).show()
                    }
                },
                windowSizeClass = windowSizeClass
            )

        }
    }

}
