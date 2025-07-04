package com.personx.cryptx.crypto

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import com.personx.cryptx.database.encryption.DatabaseProvider
import net.zetetic.database.sqlcipher.SQLiteDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


/**
 * PinCryptoManager is responsible for managing PIN-based encryption and decryption of sensitive data.
 * It allows setting up a PIN, verifying the PIN, and retrieving the raw key if the PIN is valid.
 */

private const val SALT = "salt"
private const val IV = "iv"
private const val ENCRYPTED_SESSION_KEY = "encryptedSessionKey"
private const val TRANSFORMATION = "AES/GCM/NoPadding"
class PinCryptoManager(private val context: Context) {

    /**
     * Sets up a PIN by generating a salt, deriving a key from the PIN, and encrypting a secret value.
     * The salt, IV, and encrypted secret are stored in SharedPreferences.
     *
     * @param pin The PIN to set up.
     */

    fun setupPin(pin: String) {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val sessionKey = KeyGenerator.getInstance("AES").apply { init(256) }
            .generateKey()
        val salt = generateSalt()
        val pinKey = deriveKeyFromPin(pin,salt)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        cipher.init(Cipher.ENCRYPT_MODE, pinKey, GCMParameterSpec(128, iv))
        val encryptedSessionKey = cipher.doFinal(sessionKey.encoded)

        prefs.edit {
            putString(SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            putString(IV, Base64.encodeToString(iv, Base64.NO_WRAP))
            putString(ENCRYPTED_SESSION_KEY, Base64.encodeToString(encryptedSessionKey, Base64.NO_WRAP))
        }

        pinKey.encoded.fill(0)
        SessionKeyManager.setSessionKey(sessionKey)
    }

    /**
     * Verifies the provided PIN by decrypting the stored secret and checking against a known value.
     *
     * @param pin The PIN to verify.
     * @return True if the PIN is valid, false otherwise.
     */

    fun verifyPin(pin: String): Boolean {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val saltString = prefs.getString(SALT, null) ?: return false
        val ivString = prefs.getString(IV, null) ?: return false
        val encryptedKeyString = prefs.getString(ENCRYPTED_SESSION_KEY, null) ?: return false

        val salt = Base64.decode(saltString, Base64.NO_WRAP)
        val iv = Base64.decode(ivString, Base64.NO_WRAP)
        val encryptedSessionKey = Base64.decode(encryptedKeyString, Base64.NO_WRAP)

        val key = deriveKeyFromPin(pin, salt)

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
            val decryptedKeyBytes = cipher.doFinal(encryptedSessionKey)

            val sessionKey = SecretKeySpec(decryptedKeyBytes, "AES")
            SessionKeyManager.setSessionKey(sessionKey)

            key.encoded.fill(0)
            decryptedKeyBytes.fill(0)
            true
        } catch (e: Exception) {
            key.encoded.fill(0)
            Log.e("PinCryptoManager", "PIN verification failed: ${e.message}")
            false
        }
    }

    /**
     * Retrieves the raw key bytes if the provided PIN is valid.
     * If the PIN is invalid, returns null.
     *
     * @param pin The PIN to validate.
     * @return The raw key bytes if the PIN is valid, null otherwise.
     */

    fun loadSessionKeyIfPinValid(pin: String): Boolean {
        val prefs = context.getSharedPreferences(
            "secure_prefs", Context.MODE_PRIVATE
        )
        val saltString = prefs.getString(SALT, null) ?: return false
        val ivString = prefs.getString(IV, null) ?: return false
        val encryptedSessionKeyString = prefs.getString(
            ENCRYPTED_SESSION_KEY, null
        ) ?: return false

        val salt = Base64.decode(saltString, Base64.NO_WRAP)
        val iv = Base64.decode(ivString, Base64.NO_WRAP)
        val encryptedSessionKey = Base64.decode(encryptedSessionKeyString, Base64.NO_WRAP)

        val pinKey = deriveKeyFromPin(pin, salt)

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, pinKey, GCMParameterSpec(128, iv))
            val sessionKeyBytes = cipher.doFinal(encryptedSessionKey)
            val sessionKey = SecretKeySpec(sessionKeyBytes, "AES")
            SessionKeyManager.setSessionKey(sessionKey)

            pinKey.encoded.fill(0)
            sessionKeyBytes.fill(0)
            true
        } catch (e: Exception) {
            pinKey.encoded.fill(0)
            Log.e("PinCryptoManager", "Decryption failed: ${e.message}", e)
            false
        }
    }

    /**
     * Changes the PIN and rekeys the database with the new PIN.
     * It validates the old PIN, derives a new key from the new PIN, and updates the database and SharedPreferences.
     *
     * @param oldPin The current PIN to validate.
     * @param newPin The new PIN to set.
     * @return True if the operation was successful, false otherwise.
     */

    fun changePinAndRekeyDatabase(oldPin: String, newPin: String): Boolean {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val dbPath = context.getDatabasePath("encrypted_history.db").absolutePath

        // 1. Validate old PIN and get current session key
        val sessionKey = SessionKeyManager.getSessionKey() ?: return false
        // 2. Create new key material
        val newSalt = generateSalt()
        val newPinKey = deriveKeyFromPin(newPin, newSalt)

        return try {
            System.loadLibrary("sqlcipher")
            // 3. Rekey database using raw bytes (most reliable)
            val db = SQLiteDatabase.openOrCreateDatabase(dbPath, sessionKey.encoded, null, null)
            try {
                // Convert new key to hex for PRAGMA rekey
                db.changePassword(newPinKey.encoded)
            } finally {
                db.close()
            }

            SessionKeyManager.setSessionKey(newPinKey)

            // 5. Re-encrypt the session key
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val newIv = ByteArray(12).also { SecureRandom().nextBytes(it) }
            cipher.init(Cipher.ENCRYPT_MODE, newPinKey, GCMParameterSpec(128, newIv))
            val newEncryptedSessionKey = cipher.doFinal(sessionKey.encoded)

            // 6. Update preferences
            prefs.edit {
                putString(SALT, Base64.encodeToString(newSalt, Base64.NO_WRAP))
                putString(IV, Base64.encodeToString(newIv, Base64.NO_WRAP))
                putString(ENCRYPTED_SESSION_KEY, Base64.encodeToString(newEncryptedSessionKey, Base64.NO_WRAP))
            }

            // 7. Refresh database instance
            DatabaseProvider.clearDatabaseInstance()
            true
        } catch (e: Exception) {
            Log.e("PinCryptoManager", "PIN change failed", e)
            // Consider restoring from backup here
            false
        } finally {
            sessionKey.encoded?.fill(0)
            newPinKey.encoded?.fill(0)
        }
    }
    /**
     * Derives a secret key from the provided PIN and salt using PBKDF2 with HMAC SHA-256.
     *
     * @param pin The PIN to derive the key from.
     * @param salt The salt to use in the key derivation.
     * @return The derived SecretKey.
     */

    private fun deriveKeyFromPin(pin: String, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(pin.toCharArray(), salt, 10000, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    /**
     * Generates a random salt for key derivation.
     *
     * @return A byte array containing the generated salt.
     */

    private fun generateSalt(): ByteArray {
        val secret = ByteArray(16)
        SecureRandom().nextBytes(secret)
        return secret
    }

}