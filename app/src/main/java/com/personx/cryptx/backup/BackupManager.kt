package com.personx.cryptx.backup

import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupManager {


    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val DB_NAME = "encrypted_history.db"
    private const val ENCRYPTED_DB_NAME = "encrypted_encrypted_history.db"
    private const val METADATA_JSON = "metadata.json"
    private const val PBKDF2_ITERATIONS = 310_000
    private const val SHA256_LENGTH = 32

    private fun deriveKeyFromPassword(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun sha256(bytes: ByteArray): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(bytes)
    }

    private fun generateSecureFileName(metadata: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(metadata.toByteArray())
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return hashBytes.joinToString("") { "%02x".format(it)}.take(24) + "_$timestamp.backupx"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun exportSecureBackup(context: Context, userPassword: String): File? {
        return try {
            val dbFile = context.getDatabasePath(DB_NAME)
            val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)

            val saltStr = prefs.getString("salt", null) ?: return null
            val ivStr = prefs.getString("iv", null) ?: return null
            val encryptedSessionKeyStr = prefs.getString("encryptedSessionKey", null) ?: return null

            val backupSalt = ByteArray(16).also { SecureRandom().nextBytes(it) }
            val userKey = deriveKeyFromPassword(userPassword, backupSalt)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val backupIv = ByteArray(12).also { SecureRandom().nextBytes(it) }
            cipher.init(Cipher.ENCRYPT_MODE, userKey, GCMParameterSpec(128, backupIv))

            val metadata = JSONObject().apply {
                put("storedSalt", saltStr)
                put("iv", ivStr)
                put("encryptedSessionKey", encryptedSessionKeyStr)
                put("backupSalt", Base64.encodeToString(backupSalt, Base64.NO_WRAP))
            }
            val rawMetadata = metadata.toString()
            val encryptedMetadata = cipher.doFinal(rawMetadata.toByteArray())

            val metadataFile = File(context.cacheDir, METADATA_JSON)
            FileOutputStream(metadataFile).use {
                it.write(backupIv)
                it.write(encryptedMetadata)
            }

            val dbBytes = dbFile.readBytes()
            val dbIv = ByteArray(12).also { SecureRandom().nextBytes(it) }
            val dbCipher = Cipher.getInstance(TRANSFORMATION)
            dbCipher.init(Cipher.ENCRYPT_MODE, userKey, GCMParameterSpec(128, dbIv))
            val encryptedDbBytes = dbCipher.doFinal(dbBytes)
            val dbHash = sha256(encryptedDbBytes)

            val encryptedDbFile = File(context.cacheDir, ENCRYPTED_DB_NAME)
            FileOutputStream(encryptedDbFile).use {
                it.write(dbIv)
                it.write(encryptedDbBytes)
                it.write(dbHash)
            }

            val backupFileName = generateSecureFileName(rawMetadata)
            val backupZip = File(context.cacheDir, backupFileName)
            ZipOutputStream(FileOutputStream(backupZip)).use { zipOutputStream ->
                zipOutputStream.putNextEntry(
                    ZipEntry(ENCRYPTED_DB_NAME)
                )
                Files.copy(encryptedDbFile.toPath(), zipOutputStream)
                zipOutputStream.closeEntry()

                zipOutputStream.putNextEntry(ZipEntry(METADATA_JSON))
                Files.copy(metadataFile.toPath(), zipOutputStream)
                zipOutputStream.closeEntry()
            }

            metadataFile.delete()
            encryptedDbFile.delete()

            backupZip.setReadable(false, false)
            backupZip.setReadable(true, true)

            userKey.encoded.fill(0)
            dbBytes.fill(0)
            encryptedDbBytes.fill(0)
            encryptedMetadata.fill(0)
            backupZip
        } catch (e: Exception) {
            Log.e("BackupManager", "Error exporting secure backup", e)
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun importSecureBackup(context: Context, backupFile: File, userPassword: String): Boolean {
        return try {
            val unzipDir = File(context.cacheDir, "restore").apply {
                mkdirs()
            }
            val zipInput = ZipFile(backupFile)
            zipInput.entries().asSequence().forEach { entry ->
                val outFile = File(unzipDir, entry.name)
                zipInput.getInputStream(entry).use { input ->
                    FileOutputStream(outFile).use { outputStream ->
                    input.copyTo(outputStream)}
                }
            }

            val encryptedDbFile = File(unzipDir, ENCRYPTED_DB_NAME)
            val metadataFile = File(unzipDir, METADATA_JSON)
            if (!encryptedDbFile.exists() || !metadataFile.exists()) return false

            val metadataBytes = Files.readAllBytes(metadataFile.toPath())
            val backupIv = metadataBytes.sliceArray(0 until 12)
            val encryptedMetadata = metadataBytes.sliceArray(12 until metadataBytes.size)

            val parsedJson = JSONObject(String(encryptedMetadata))
            val backupSaltStr = parsedJson.getString("backupSalt")
            val backupSalt = Base64.decode(backupSaltStr, Base64.NO_WRAP)

            val userKey = deriveKeyFromPassword(userPassword, backupSalt)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, userKey, GCMParameterSpec(128, backupIv))
            val decryptedMetadata = cipher.doFinal(encryptedMetadata)
            val metadataJson = JSONObject(String(decryptedMetadata))

            val newSalt = metadataJson.getString("storedSalt")
            val iv = metadataJson.getString("iv")
            val encryptedSessionKey = metadataJson.getString("encryptedSessionKey")

            val dbFileBytes = Files.readAllBytes(encryptedDbFile.toPath())
            val dbIv = dbFileBytes.sliceArray(0 until 12)
            val dbEncryptedBytes = dbFileBytes.sliceArray(12 until dbFileBytes.size - SHA256_LENGTH)
            val dbExpectedHash = dbFileBytes.sliceArray((dbFileBytes.size - SHA256_LENGTH until dbFileBytes.size))

            val dbCipher = Cipher.getInstance(TRANSFORMATION)
            dbCipher.init(Cipher.DECRYPT_MODE, userKey, GCMParameterSpec(128, dbIv))
            val decryptedDbBytes = dbCipher.doFinal(dbEncryptedBytes)

            val actualHash = sha256(decryptedDbBytes)
            if (!dbExpectedHash.contentEquals(actualHash)) {
                Log.e("BackupManager", "Database hash mismatch during import")
                return false
            }
            val dbDest = context.getDatabasePath(DB_NAME)
            dbDest.delete()
            FileOutputStream(dbDest).use { it.write(decryptedDbBytes) }

            context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE).edit().apply {
                putString("salt", newSalt)
                putString("iv", iv)
                putString("encryptedSessionKey", encryptedSessionKey)
                apply()
            }

            userKey.encoded.fill(0)
            decryptedMetadata.fill(0)
            dbEncryptedBytes.fill(0)
            decryptedDbBytes.fill(0)
            encryptedMetadata.fill(0)

            unzipDir.deleteRecursively()
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Error importing secure backup", e)
            false
        }
    }
}