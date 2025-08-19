package com.personx.cryptx.viewmodel.fileencryption

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.personx.cryptx.crypto.SessionKeyManager
import com.personx.cryptx.crypto.VaultManager
import com.personx.cryptx.data.VaultMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// Adjusted to use timestamp as Long
data class VaultFile(
    val name: String,
    val size: Long,
    val lastModified: Long, // <-- now a timestamp
    val mimeType: String,
    val folderName: String? = null // optional virtual folder support
)

class VaultViewModel(
    private val repo: VaultRepository
) : ViewModel() {

    private val _files = MutableStateFlow<List<VaultFile>>(emptyList())
    val files: StateFlow<List<VaultFile>> = _files

    // Track current folder path. "" = root
    private val _currentFolder = MutableStateFlow("")
    val currentFolder: StateFlow<String> = _currentFolder

    fun addFile(uri: Uri, contentResolver: ContentResolver, folder: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val name = sanitizeFileName(uri.lastPathSegment ?: "unknown")
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            val now = System.currentTimeMillis()

            val metadata = VaultMetadata(
                fileName = name,
                folderName = folder,
                mimeType = mimeType,
                createdAt = now,
                modifiedAt = now,
                size = getFileSize(contentResolver, uri)
            )

            contentResolver.openInputStream(uri)?.use { input ->
                repo.saveFile(input, metadata)
            }
            loadFiles(folder) // reload current folder
        }
    }

    fun getFileSize(contentResolver: ContentResolver, uri: Uri): Long {
        contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) return cursor.getLong(sizeIndex)
            }
        }
        return -1L
    }

    fun addFile(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            val folder = _currentFolder.value
            val name = sanitizeFileName(uri.lastPathSegment ?: "unknown")
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            val now = System.currentTimeMillis()

            val metadata = VaultMetadata(
                fileName = name,
                folderName = folder,
                mimeType = mimeType,
                createdAt = now,
                modifiedAt = now,
                size = getFileSize(contentResolver, uri)
            )

            contentResolver.openInputStream(uri)?.use { input ->
                repo.saveFile(input, metadata)
            }
            loadFiles()
        }
    }

    private fun sanitizeFileName(uriLastPath: String): String {
        return uriLastPath
            .substringAfterLast('/')
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
    }

    fun deleteFile(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFile(fileName, _currentFolder.value)
            loadFiles()
        }
    }

    fun loadFiles(folder: String = _currentFolder.value) {
        viewModelScope.launch(Dispatchers.IO) {
            val allFiles = repo.listFiles()
            _files.value = allFiles
                .filter { it.folderName == folder }
                .distinctBy { it.fileName to it.mimeType } // ✅ prevents duplicates
                .map { meta ->
                    VaultFile(
                        name = meta.fileName,
                        size = meta.size,
                        lastModified = meta.modifiedAt,
                        mimeType = meta.mimeType,
                        folderName = meta.folderName
                    )
                }
        }
    }

    fun deleteFolder(folderName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFolder(folderName, _currentFolder.value)
            loadFiles()
        }
    }

    fun downloadFile(file: VaultFile) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repo.downloadFile(file)
            }

            withContext(Dispatchers.Main) {
                if (result != null) {
                    val path = result.first?.absolutePath ?: result.second.toString()
                    Log.d("VaultViewModel", "File downloaded: $path")
                } else {
                    Log.d("VaultViewModel", "File download failed or file not found")
                }
            }
        }
    }

    fun createFolder(folderName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (folderName.isNotBlank()) {
                val current = _currentFolder.value
                val allFiles = repo.listFiles()

                // Check if folder with same name already exists in current folder
                val exists = allFiles.any {
                    it.folderName == current &&
                            it.fileName == folderName &&
                            it.mimeType == "folder"
                }

                if (!exists) {
                    repo.createFolder(folderName, current)
                } else {
                    // You can show a Toast or emit an error state here
                    Log.w("VaultViewModel", "Folder already exists: $folderName")
                }
                loadFiles()
            }
        }
    }

    // --- Navigation helpers ---
    fun openFolder(folderPath: String) {
        _currentFolder.value = folderPath
        loadFiles()
    }


    fun goUp() {
        val current = _currentFolder.value
        _currentFolder.value = current.substringBeforeLast('/', "")
        loadFiles()
    }
}



