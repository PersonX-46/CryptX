package com.personx.cryptx.screens.fileencryption

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.personx.cryptx.components.CyberpunkButton
import com.personx.cryptx.components.Header
import com.personx.cryptx.viewmodel.fileencryption.VaultFile
import com.personx.cryptx.viewmodel.fileencryption.VaultViewModel
import java.util.Date

@Composable
fun VaultScreen(
    viewModel: VaultViewModel,
    windowSizeClass: WindowSizeClass,
    onFileClick: (VaultFile) -> Unit
) {
    val context = LocalContext.current
    val files by viewModel.files.collectAsState()
    val currentFolder by viewModel.currentFolder.collectAsState()

    val cyberGreen = Color(0xFF00FF9D)
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 16.dp else 24.dp
    val spacing = if (isCompact) 16.dp else 24.dp

    var showFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    // Load files initially
    LaunchedEffect(currentFolder) {
        viewModel.loadFiles(currentFolder)
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.addFile(it, context.contentResolver, currentFolder)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Header("FILE VAULT", windowSizeClass = windowSizeClass)

        // Breadcrumbs & back button
        Column(modifier = Modifier.padding(horizontal = padding, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.goUp() },
                    enabled = currentFolder.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = cyberGreen)
                }

                Spacer(modifier = Modifier.width(8.dp))

                val parts = if (currentFolder.isEmpty()) listOf("Root") else currentFolder.split("/")
                ScrollableBreadcrumb(
                    currentFolder = currentFolder,
                    onNavigate = { viewModel.openFolder(it) },
                    cyberGreen = cyberGreen
                )
            }
        }

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
                )
                .padding(padding)
        ) {
            // Buttons: Create Folder & Upload File
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CyberpunkButton(
                    onClick = { showFolderDialog = true },
                    icon = Icons.Default.CreateNewFolder,
                    text = "Create Folder",
                )

                CyberpunkButton(
                    onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                    icon = Icons.Default.CloudUpload,
                    text = "Upload File",
                )
            }

            Spacer(modifier = Modifier.height(spacing))

            // Folder creation dialog
            if (showFolderDialog) {
                AlertDialog(
                    onDismissRequest = { showFolderDialog = false },
                    title = { Text("New Folder") },
                    text = {
                        TextField(
                            value = newFolderName,
                            onValueChange = { newFolderName = it },
                            placeholder = { Text("Folder Name") }
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (newFolderName.isNotBlank()) {
                                viewModel.createFolder(newFolderName)
                                newFolderName = ""
                                showFolderDialog = false
                            } else {
                                Toast.makeText(context, "Folder name cannot be empty", Toast.LENGTH_SHORT).show()
                            }
                        }) { Text("Create") }
                    },
                    dismissButton = {
                        Button(onClick = { showFolderDialog = false }) { Text("Cancel") }
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing))

            // Files / Folders list
            if (files.isEmpty()) {
                EmptyVaultCard(cyberGreen, windowSizeClass)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = spacing),
                    verticalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    items(files) { file ->
                        VaultFileRow(
                            file = file,
                            cyberpunkGreen = cyberGreen,
                            onClick = {
                                if (file.mimeType == "folder") {
                                    viewModel.openFolder(if (currentFolder.isEmpty()) file.name else "${currentFolder}/${file.name}")
                                } else {
                                    onFileClick(file)
                                }
                            },
                            onDownload = { viewModel.downloadFile(file) },
                            onDelete = { viewModel.deleteFile(file.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScrollableBreadcrumb(
    currentFolder: String,
    onNavigate: (String) -> Unit,
    cyberGreen: Color
) {
    val scrollState = rememberScrollState()
    val parts = if (currentFolder.isEmpty()) listOf("Root") else currentFolder.split("/")
    var pathSoFar = ""

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        parts.forEachIndexed { index, part ->
            if (index > 0) pathSoFar += "/"
            pathSoFar += part

            Text(
                text = part,
                color = cyberGreen,
                modifier = Modifier
                    .clickable { onNavigate(pathSoFar) }
                    .padding(horizontal = 4.dp),
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodyMedium
            )
            if (index < parts.lastIndex) {
                Text(" / ", color = cyberGreen.copy(alpha = 0.6f))
            }
        }
    }
}


@Composable
fun VaultFileRow(
    file: VaultFile,
    cyberpunkGreen: Color,
    onClick: () -> Unit,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, cyberpunkGreen.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when {
                file.mimeType == "folder" -> Icons.Outlined.Folder
                file.mimeType.startsWith("image/") -> Icons.Outlined.Image
                file.mimeType.startsWith("video/") -> Icons.Outlined.Videocam
                file.mimeType.startsWith("audio/") -> Icons.Outlined.MusicNote
                file.mimeType == "application/pdf" -> Icons.Default.PictureAsPdf
                else -> Icons.Outlined.Description
            }

            Icon(imageVector = icon, contentDescription = null, tint = cyberpunkGreen, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyMedium.copy(color = cyberpunkGreen, fontFamily = FontFamily.Monospace),
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (file.mimeType == "folder") "Folder" else "${file.size / 1024} KB • ${file.lastModified}",
                    style = MaterialTheme.typography.labelSmall.copy(color = cyberpunkGreen.copy(alpha = 0.6f), fontFamily = FontFamily.Monospace)
                )
            }

            // Download button (only for files)
            if (file.mimeType != "folder") {
                IconButton(onClick = onDownload) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = cyberpunkGreen)
                }
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}



@Composable
fun VaultFileCard(
    file: VaultFile,
    cyberpunkGreen: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, cyberpunkGreen.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Select icon based on MIME type
            val icon = when {
                file.mimeType.startsWith("image/") -> Icons.Default.Image
                file.mimeType.startsWith("video/") -> Icons.Default.Movie
                file.mimeType.startsWith("audio/") -> Icons.Default.MusicNote
                file.mimeType == "application/pdf" -> Icons.Default.PictureAsPdf
                else -> Icons.Default.Description
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = cyberpunkGreen,
                modifier = Modifier.size(36.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = cyberpunkGreen,
                        fontFamily = FontFamily.Monospace
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "Last Modified: ${Date(file.lastModified)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = cyberpunkGreen.copy(alpha = 0.6f),
                        fontFamily = FontFamily.Monospace
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = "${file.size / 1024} KB",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = cyberpunkGreen.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}


@Composable
fun EmptyVaultCard(cyberpunkGreen: Color, windowSizeClass: WindowSizeClass) {
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val padding = if (isCompact) 14.dp else 18.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, cyberpunkGreen.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vault is empty",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = cyberpunkGreen.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap the + button to upload files",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}
