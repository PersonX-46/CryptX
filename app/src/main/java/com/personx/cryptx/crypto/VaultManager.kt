package com.personx.cryptx.crypto

import android.content.Context
import android.util.Base64
import com.personx.cryptx.data.VaultMetadata
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class VaultManager(private val context: Context) {
    private val vaultDir = File(context.filesDir, "vault").apply { mkdirs() }

    private val AES_MODE = "AES/GCM/NoPadding"
    private val GCM_TAG_BITS = 128
    private val IV_BYTES = 12

    // ---- DEK (wrapped by master/session key) ----
    private val dek: SecretKey by lazy { loadOrCreateDek() }

    // ---- Key separation (tiny KDF) ----
    // PRK = HMAC(DEK, "KDF"); then derive subkeys by label
    // PRK = HMAC(DEK, "KDF")
    private val prk: ByteArray by lazy { hmacSha256(dek.encoded, "KDF".toByteArray()) }

    // filename key bytes = HMAC(PRK, "NAME")
    private val filenameKeyBytes: ByteArray by lazy { hmacSha256(prk, "NAME".toByteArray()) }

    // deterministic filename HMAC key (not used for AES)
    private val filenameMac: Mac by lazy {
        Mac.getInstance("HmacSHA256").apply {
            init(SecretKeySpec(hmacSha256(prk, "NAME".toByteArray()), "HmacSHA256"))
        }
    }

    // metadata AE key (AES-256)
    private val metaKey: SecretKey by lazy {
        SecretKeySpec(hmacSha256(prk, "META".toByteArray()).copyOf(32), "AES")
    }

    // derive per-file AE key from token
    private fun fileKey(token: String): SecretKey {
        val material = hmacSha256(prk, ("FILE:" + token).toByteArray())
        return SecretKeySpec(material.copyOf(32), "AES")
    }

    fun vaultExists(): Boolean = vaultDir.exists() && vaultDir.isDirectory

    fun createVault() {
        if (!vaultExists()) vaultDir.mkdirs()
        loadOrCreateDek() // ensure DEK exists
    }

    fun addFile(input: InputStream, metadata: VaultMetadata, overwrite: Boolean = false) {
        val token = fileToken(metadata.folderName, metadata.fileName)
        val metaFile = File(vaultDir, "$token.meta")
        val dataFile = File(vaultDir, "$token.bin")

        if (!overwrite && (metaFile.exists() || dataFile.exists())) {
            throw IOException("File already exists in vault")
        }

        // 1. Encrypt metadata
        val metaJson = metadata.toJson().toByteArray()
        val metaEnc = encryptAes(metaJson, metaKey, aad = token.toByteArray())
        metaFile.writeBytes(metaEnc)

        // 2. Encrypt file content with proper HMAC calculation
        val fKey = fileKey(token)
        val hmacKey = SecretKeySpec(hmacSha256(prk, ("HMAC:" + token).toByteArray()), "HmacSHA256")
        val hmac = Mac.getInstance("HmacSHA256").apply { init(hmacKey) }

        val iv = ByteArray(16).also { SecureRandom().nextBytes(it) } // AES block size

        FileOutputStream(dataFile).use { fout ->
            // Write IV first
            fout.write(iv)
            hmac.update(iv)

            // Initialize cipher
            val cipher = Cipher.getInstance(AES_MODE).apply {
                init(Cipher.ENCRYPT_MODE, fKey, IvParameterSpec(iv))
            }

            // Encrypt in chunks and update HMAC
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                val encrypted = cipher.update(buffer, 0, bytesRead)
                fout.write(encrypted)
                hmac.update(encrypted) // HMAC of ciphertext
            }

            // Finalize encryption
            val finalEncrypted = cipher.doFinal()
            if (finalEncrypted != null) {
                fout.write(finalEncrypted)
                hmac.update(finalEncrypted)
            }

            // Write HMAC tag at the end
            fout.write(hmac.doFinal())
        }
    }

    fun readFileBytes(fileName: String, folderName: String = ""): ByteArray {
        val token = fileToken(folderName, fileName)
        val dataFile = File(vaultDir, "$token.bin")
        if (!dataFile.exists()) throw IOException("File not found")

        val fKey = fileKey(token)
        val hmacKey = SecretKeySpec(
            hmacSha256(prk, ("HMAC:" + token).toByteArray()),
            "HmacSHA256"
        )

        FileInputStream(dataFile).use { fin ->
            // 1. Read IV
            val iv = ByteArray(16)
            if (fin.read(iv) != iv.size) throw IOException("Invalid IV")

            // 2. Initialize HMAC with IV
            val hmac = Mac.getInstance("HmacSHA256").apply {
                init(hmacKey)
                update(iv)
            }

            // 3. Init cipher
            val cipher = Cipher.getInstance(AES_MODE).apply {
                init(Cipher.DECRYPT_MODE, fKey, IvParameterSpec(iv))
            }

            // 4. Work out sizes
            val fileSize = dataFile.length()
            val hmacLength = hmac.macLength
            val contentLength = fileSize - iv.size - hmacLength

            val buffer = ByteArray(8192)
            val decryptedOut = ByteArrayOutputStream()
            var remaining = contentLength

            while (remaining > 0) {
                val chunkSize = minOf(buffer.size.toLong(), remaining).toInt()
                val read = fin.read(buffer, 0, chunkSize)
                if (read == -1) throw IOException("Unexpected EOF")

                hmac.update(buffer, 0, read) // HMAC over ciphertext
                val decrypted = cipher.update(buffer, 0, read)
                decryptedOut.write(decrypted)

                remaining -= read
            }

            // Finalize cipher
            decryptedOut.write(cipher.doFinal())

            // Verify HMAC
            val expectedTag = ByteArray(hmacLength)
            fin.read(expectedTag)
            if (!hmac.doFinal().contentEquals(expectedTag)) {
                throw IOException("HMAC verification failed")
            }

            return decryptedOut.toByteArray()
        }
    }


    fun saveFolderMetadata(metadata: VaultMetadata) {
        // Use the same fileToken system
        val token = fileToken(metadata.folderName, metadata.fileName)
        val metaFile = File(vaultDir, "$token.meta")

        // Convert metadata to JSON and encrypt it
        val metaJson = metadata.toJson().toByteArray()
        val metaEnc = encryptAes(metaJson, metaKey, aad = token.toByteArray())
        metaFile.writeBytes(metaEnc)
    }

    fun createFolder(folderName: String, parentFolder: String = ""): Boolean {
        val folder = if (parentFolder.isEmpty()) {
            File(vaultDir, folderName)
        } else {
            File(vaultDir, "$parentFolder/$folderName")
        }
        if (!folder.exists()) return folder.mkdirs()
        return true
    }


    /** List files by decrypting each *.meta; AAD = token (from filename). */
    fun listFiles(): List<VaultMetadata> {
        val out = mutableListOf<VaultMetadata>()

        // Existing metadata files
        vaultDir.listFiles { f -> f.isFile && f.extension == "meta" }?.forEach { metaFile ->
            val token = metaFile.name.removeSuffix(".meta")
            val enc = metaFile.readBytes()
            val plain = decryptAes(enc, metaKey, aad = token.toByteArray())
            out += VaultMetadata.fromJson(String(plain))
        }

        // Include real folders in vaultDir
        vaultDir.listFiles { f -> f.isDirectory }?.forEach { dir ->
            out += VaultMetadata(
                fileName = dir.name,
                folderName = "", // parent folder, adjust if you support nested folders
                mimeType = "folder",
                createdAt = dir.lastModified(),
                modifiedAt = dir.lastModified(),
                size = 0L
            )
        }

        return out
    }


    /** Delete by original names — we recompute token the same way. */
    fun deleteFile(fileName: String, folderName: String = "") {
        val token = fileToken(folderName, fileName)
        File(vaultDir, "$token.meta").delete()
        File(vaultDir, "$token.bin").delete()
    }

    /** Delete a folder and everything inside it (recursively). */
    fun deleteFolder(folderName: String, parentFolder: String = ""): Boolean {
        val folder = if (parentFolder.isEmpty()) {
            File(vaultDir, folderName)
        } else {
            File(vaultDir, "$parentFolder/$folderName")
        }
        if (!folder.exists() || !folder.isDirectory) return false

        // Recursively delete everything inside
        folder.walkBottomUp().forEach { file ->
            file.delete()
        }

        // Also delete any metadata that matches this folder
        val token = fileToken(parentFolder, folderName)
        File(vaultDir, "$token.meta").delete()

        return !folder.exists()
    }


    // ---------- Filename token (deterministic & opaque) ----------
    /**
     * token = Base64Url( HMAC-SHA256(filenameKey, folderName + "/" + fileName) )
     * Fixed-length; reveals duplicates only.
     */
    private fun fileToken(folderName: String?, fileName: String): String {
        val normalized = (folderName ?: "").ifEmpty { "" } + "/" + fileName

        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(filenameKeyBytes, "HmacSHA256"))
        val digest = mac.doFinal(normalized.toByteArray())

        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP)
    }
    // ---------- AES-GCM helpers (with optional AAD) ----------

    private fun encryptAes(plain: ByteArray, key: SecretKey, aad: ByteArray? = null): ByteArray {
        val iv = ByteArray(IV_BYTES).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        if (aad != null) cipher.updateAAD(aad)
        val ct = cipher.doFinal(plain)
        return iv + ct
    }

    private fun decryptAes(ivPlusCt: ByteArray, key: SecretKey, aad: ByteArray? = null): ByteArray {
        require(ivPlusCt.size > IV_BYTES) { "cipher too short" }
        val iv = ivPlusCt.copyOfRange(0, IV_BYTES)
        val ct = ivPlusCt.copyOfRange(IV_BYTES, ivPlusCt.size)
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        if (aad != null) cipher.updateAAD(aad)
        return cipher.doFinal(ct)
    }

    // ---------- Key management ----------

    private fun loadOrCreateDek(): SecretKey {
        val master = SessionKeyManager.getSessionKey()
            ?: throw IllegalStateException("Master key (session) not available")

        val dekFile = File(vaultDir, "dek.bin")
        return if (dekFile.exists()) {
            val enc = dekFile.readBytes()
            val plain = decryptAes(enc, master)
            SecretKeySpec(plain, "AES")
        } else {
            val newDek = generateAesKey(256)
            val enc = encryptAes(newDek.encoded, master)
            dekFile.writeBytes(enc)
            newDek
        }
    }

    private fun generateAesKey(bits: Int): SecretKey {
        val kg = KeyGenerator.getInstance("AES")
        kg.init(bits, SecureRandom())
        return kg.generateKey()
    }

    // ---------- HMAC-SHA256 helper ----------

    private fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }
}



