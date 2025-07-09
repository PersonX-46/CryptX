package com.personx.cryptx.backup

import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.personx.cryptx.crypto.SessionKeyManager
import com.personx.cryptx.database.encryption.DatabaseProvider
import net.zetetic.database.sqlcipher.SQLiteDatabase
import okhttp3.internal.userAgent
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.security.SecureRandom
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.Destroyable

@RequiresApi(Build.VERSION_CODES.O)
object BackupManager {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val DB_NAME = "encrypted_history.db"
    private const val BACKUP_PREFIX = "secure_backup_"
    private const val METADATA_FILE = "metadata.enc"
    private const val DB_FILE = "database.enc"

    private const val BACKUP_SALT_FILE = "backup_salt.bin"
    private const val PBKDF2_ITERATIONS = 310_000
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 12
    private const val TAG_LENGTH = 16 // GCM tag length

    private fun secureDelete(file: File) {
        try {
            if (file.exists()) {
                // Overwrite with random data before deletion
                RandomAccessFile(file, "rws").use { raf ->
                    val buffer = ByteArray(4096)
                    SecureRandom().nextBytes(buffer)
                    val length = raf.length()
                    var remaining = length
                    while (remaining > 0) {
                        val toWrite = minOf(remaining, buffer.size.toLong())
                        raf.write(buffer, 0, toWrite.toInt())
                        remaining -= toWrite
                    }
                }
                file.delete()
            }
        } catch (e: Exception) {
            Log.w("SecureDelete", "Failed to securely delete file", e)
            file.delete() // Fallback to normal delete
        }
    }

    private fun deriveSecureKey(password: String, salt: ByteArray): SecretKey {
        return SecretKeySpec(
            PBEKeySpec(
                password.toCharArray(),
                salt,
                PBKDF2_ITERATIONS,
                256
            ).let { spec ->
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .encoded
            },
            "AES"
        )
    }

    @Throws(IOException::class)
    private fun createSecureBackupFile(context: Context): File {
        return File.createTempFile(
            BACKUP_PREFIX,
            ".backup",
            context.getExternalFilesDir(null) ?: context.filesDir
        ).apply {
            setReadable(true, true)
            setWritable(true, true)
        }
    }

    fun exportBackup(context: Context, password: String): File? {
        DatabaseProvider.clearDatabaseInstance()
        if (password.isEmpty()) return null

        // 1. Prepare encryption materials
        val backupSalt = ByteArray(SALT_LENGTH).apply { SecureRandom().nextBytes(this) }
        val userKey = deriveSecureKey(password, backupSalt)

        // 2. Create secure temp files
        val tempDir = File(context.cacheDir, "backup_temp_${System.currentTimeMillis()}").apply {
            mkdirs()
        }

        return try {
            // 3. Encrypt metadata
            val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
            val metadata = JSONObject().apply {
                put("salt", prefs.getString("salt", "") ?: "")
                put("iv", prefs.getString("iv", "") ?: "")
                put("encryptedKey", prefs.getString("encryptedSessionKey", "") ?: "")
                put("version", 1)
            }

            val metadataBytes = metadata.toString().toByteArray()
            val metadataIv = ByteArray(IV_LENGTH).apply { SecureRandom().nextBytes(this) }
            val encryptedMetadata = Cipher.getInstance(TRANSFORMATION).run {
                init(Cipher.ENCRYPT_MODE, userKey, GCMParameterSpec(128, metadataIv))
                doFinal(metadataBytes)
            }
            // 4. Encrypt database
            System.loadLibrary("sqlcipher")
            // 3. Rekey database using raw bytes (most reliable)
            if (SessionKeyManager.getSessionKey() != null){
                val sessionKey = SessionKeyManager.getSessionKey() ?: return null
                val db = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(
                    context.getDatabasePath(DB_NAME).absolutePath),
                    sessionKey.encoded,
                    null,
                    null
                )
                try {
                    // Convert new key to hex for PRAGMA rekey
                    db.rawQuery("PRAGMA wal_checkpoint(FULL)", null)
                } finally {
                    db.close()
                }
            }
            val dbFile = context.getDatabasePath(DB_NAME)
            val dbIv = ByteArray(IV_LENGTH).apply { SecureRandom().nextBytes(this) }
            val encryptedDb = Cipher.getInstance(TRANSFORMATION).run {
                init(Cipher.ENCRYPT_MODE, userKey, GCMParameterSpec(128, dbIv))
                doFinal(dbFile.readBytes())
            }

            // 5. Write to temp files
            File(tempDir, BACKUP_SALT_FILE).writeBytes(backupSalt)
            File(tempDir, METADATA_FILE).writeBytes(metadataIv + encryptedMetadata)
            File(tempDir, DB_FILE).writeBytes(dbIv + encryptedDb)

            // 6. Create final backup archive
            val backupFile = createSecureBackupFile(context)

            ZipOutputStream(FileOutputStream(backupFile)).use { zip ->
                zip.putNextEntry(ZipEntry(METADATA_FILE))
                File(tempDir, METADATA_FILE).inputStream().use { it.copyTo(zip) }
                zip.closeEntry()

                zip.putNextEntry(ZipEntry(DB_FILE))
                File(tempDir, DB_FILE).inputStream().use { it.copyTo(zip) }
                zip.closeEntry()

                zip.putNextEntry(ZipEntry(BACKUP_SALT_FILE))
                File(tempDir, BACKUP_SALT_FILE).inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
            }

            backupFile
        } catch (e: Exception) {
            Log.e("BackupManager", "Export failed", e)
            null
        } finally {
            tempDir.listFiles()?.forEach { secureDelete(it) }
            tempDir.delete()
            userKey.destroyKeyMaterial()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun importBackup(context: Context, backupFile: File, password: String): Boolean {
        if (password.isEmpty()) {
            Log.e("BackupManager", "❌ Password is empty")
            return false
        }

        val tempDir = File(context.cacheDir, "restore_temp_${System.currentTimeMillis()}").apply {
            mkdirs()
        }

        try {
            // Step 1: Unzip the backup file
            Log.d("BackupManager", "🔍 Unzipping backup file: ${backupFile.absolutePath}")
            ZipFile(backupFile).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val outFile = File(tempDir, entry.name)
                    outFile.outputStream().use { output ->
                        zip.getInputStream(entry).use { input ->
                            input.copyTo(output)
                        }
                    }
                    Log.d("BackupManager", "✅ Extracted file: ${outFile.absolutePath} (${outFile.length()} bytes)")
                }
            }

            val metadataFile = File(tempDir, METADATA_FILE)
            val dbFile = File(tempDir, DB_FILE)

            Log.d("BackupManager", "📄 Checking extracted files...")
            if (!metadataFile.exists()) {
                Log.e("BackupManager", "❌ Metadata file missing")
                return false
            }
            if (!dbFile.exists()) {
                Log.e("BackupManager", "❌ Encrypted DB file missing")
                return false
            }

            val backupSaltFile = File(tempDir, BACKUP_SALT_FILE)
            val backupSalt = backupSaltFile.readBytes()
            Log.d("BackupManager", "🔐 Deriving user key using backupSalt")

            val userKey = deriveSecureKey(password, backupSalt)

            try {
                val metadataBytes = metadataFile.readBytes()
                if (metadataBytes.size < IV_LENGTH) {
                    Log.e("BackupManager", "Metadata file too short")
                    return false
                }

                val metadataIV = metadataBytes.copyOfRange(0, IV_LENGTH)
                val encryptedMetadata = metadataBytes.copyOfRange(IV_LENGTH, metadataBytes.size)
                val decryptedMetadataBytes = Cipher.getInstance(TRANSFORMATION).run {
                    init(Cipher.DECRYPT_MODE, userKey, GCMParameterSpec(128, metadataIV))
                    doFinal(encryptedMetadata)
                }
                val metadataJson = String(decryptedMetadataBytes)
                val metadata = JSONObject(metadataJson)

                val dbBytes = dbFile.readBytes()
                Log.d("BackupManager", "📦 Encrypted DB file size: ${dbBytes.size}")
                if (dbBytes.size < IV_LENGTH) {
                    Log.e("BackupManager", "❌ DB file too short")
                    return false
                }

                val dbIv = dbBytes.copyOfRange(0, IV_LENGTH)
                val encryptedDb = dbBytes.copyOfRange(IV_LENGTH, dbBytes.size)

                Log.d("BackupManager", "🔓 Attempting DB decryption")
                val decryptedDb = Cipher.getInstance(TRANSFORMATION).run {
                    init(Cipher.DECRYPT_MODE, userKey, GCMParameterSpec(128, dbIv))
                    doFinal(encryptedDb)
                }
                Log.d("BackupManager", "✅ DB decrypted successfully")

                val targetDb = context.getDatabasePath(DB_NAME).apply {
                    parentFile?.mkdirs()
                    delete()
                }
                FileOutputStream(targetDb).use { it.write(decryptedDb) }
                Log.d("BackupManager", "💾 Restored DB to: ${targetDb.absolutePath}")

                context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE).edit {
                    putString("salt", metadata.getString("salt"))
                    putString("iv", metadata.getString("iv"))
                    putString("encryptedSessionKey", metadata.getString("encryptedKey"))
                    commit()
                }
                Log.d("BackupManager", "✅ Secure prefs restored")

                DatabaseProvider.clearDatabaseInstance()
                return true
            } catch (e: Exception) {
                Log.e("BackupManager", "❌ DB decryption failed", e)
                return false
            } finally {
                userKey.destroyKeyMaterial()
            }

        } catch (e: Exception) {
            Log.e("BackupManager", "❌ General import failure", e)
            return false
        } finally {
            tempDir.listFiles()?.forEach { secureDelete(it) }
            tempDir.delete()
            Log.d("BackupManager", "🧹 Temp directory cleaned up")
        }
    }


    // Extension to help clear key material
    private fun SecretKey.destroyKeyMaterial() {
        try {
            (this as? Destroyable)?.destroy()
        } catch (e: Exception) {
            // Best effort cleanup
            (this as? SecretKeySpec)?.encoded?.fill(0)
        }
    }
}