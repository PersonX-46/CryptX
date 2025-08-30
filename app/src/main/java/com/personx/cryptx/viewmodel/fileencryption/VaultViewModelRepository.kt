package com.personx.cryptx.viewmodel.fileencryption

import android.content.Context
import android.net.Uri
import com.personx.cryptx.AppFileManager
import com.personx.cryptx.crypto.VaultManager
import com.personx.cryptx.data.VaultMetadata
import java.io.File
import java.io.InputStream

class VaultRepository(private val context: Context) {

    private val vaultManager = VaultManager(context)

    fun saveFile(input: InputStream, metadata: VaultMetadata) {
        vaultManager.addFile(input, metadata)
    }

    fun listFiles(): List<VaultMetadata> {
        return vaultManager.listFiles()
    }

    fun createVaultIfNeeded() {
        if (!vaultManager.vaultExists()) {
            vaultManager.createVault()
        }
    }

    fun createFolder(folderName: String, parentFolder: String = ""): Boolean {
        val created = vaultManager.createFolder(folderName, parentFolder)
        if (created) {
            // Save metadata for this folder
            val now = System.currentTimeMillis()
            val metadata = VaultMetadata(
                fileName = folderName,
                folderName = parentFolder,
                mimeType = "folder",
                createdAt = now,
                modifiedAt = now,
                size = 0L
            )
            vaultManager.saveFolderMetadata(metadata)
        }
        return created
    }

    fun downloadFile(file: VaultFile): Pair<File?, Uri?>? {
        return try {
            val bytes = vaultManager.readFileBytes(file.name, file.folderName ?: "")
            AppFileManager.saveToPublicDirectory(
                context = context,
                subPath = "cryptx/vault",
                filename = file.name,
                content = bytes,
                mimeType = file.mimeType
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun deleteFile(fileName: String, folderName: String = "") {
        vaultManager.deleteFile(fileName, folderName)
    }

    fun deleteFolder(folderName: String, parentFolder: String = "") {
        vaultManager.deleteFolder(folderName, parentFolder)
    }

}



