package com.personx.cryptx.viewmodel.fileencryption

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.personx.cryptx.AppFileManager
import com.personx.cryptx.components.Toast
import com.personx.cryptx.crypto.VaultManager
import com.personx.cryptx.data.VaultMetadata
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

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



