package com.personx.cryptx.crypto

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import com.personx.cryptx.database.encryption.DatabaseProvider
import net.sqlcipher.database.SQLiteDatabase
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


/**
 * PinCryptoManager is responsible for managing PIN-based encryption and decryption of sensitive data.
 * It allows setting up a PIN, verifying the PIN, and retrieving the raw key if the PIN is valid.
 */
class PinCryptoManager(private val context: Context) {

    private val authSecret = "auth_secret_salt"

    /**
     * Sets up a PIN by generating a salt, deriving a key from the PIN, and encrypting a secret value.
     * The salt, IV, and encrypted secret are stored in SharedPreferences.
     *
     * @param pin The PIN to set up.
     */

    fun setupPin(pin: String) {
        val salt = generateSalt()
        val key = deriveKeyFromPin(pin, salt)
        val secret = authSecret.toByteArray()
        val (encryptedSecret, iv) = encryptSecret(secret, key)

        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        prefs.edit {
            putString("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
            putString("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
            putString("secret", Base64.encodeToString(encryptedSecret, Base64.NO_WRAP))
        }
    }

    /**
     * Verifies the provided PIN by decrypting the stored secret and checking against a known value.
     *
     * @param pin The PIN to verify.
     * @return True if the PIN is valid, false otherwise.
     */

    fun verifyPin(pin: String): Boolean {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val saltString = prefs.getString("salt", null) ?: return false
        val ivString = prefs.getString("iv", null) ?: return false
        val secretString = prefs.getString("secret", null) ?: return false

        val salt = Base64.decode(saltString, Base64.NO_WRAP)
        val iv = Base64.decode(ivString, Base64.NO_WRAP)
        val encryptedSecret = Base64.decode(secretString, Base64.NO_WRAP)

        val key = deriveKeyFromPin(pin, salt)

        return try {
            val decryptedSecret = decryptSecret(encryptedSecret, iv, key)
            Log.d("decryptedSecret", decryptedSecret.toString())
            return decryptedSecret == authSecret // Check against a known value
        } catch (e: Exception) {
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

    fun getRawKeyIfPinValid(pin: String): ByteArray? {
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val saltString = prefs.getString("salt", null) ?: return null
        val ivString = prefs.getString("iv", null) ?: return null
        val secretString = prefs.getString("secret", null) ?: return null

        val salt = Base64.decode(saltString, Base64.NO_WRAP)
        val iv = Base64.decode(ivString, Base64.NO_WRAP)
        val encryptedSecret = Base64.decode(secretString, Base64.NO_WRAP)

        val key = deriveKeyFromPin(pin, salt)

        return try {
            val decryptedSecret = decryptSecret(encryptedSecret, iv, key)
            if (decryptedSecret == authSecret) {
                key.encoded // return raw key bytes
            } else {
                null
            }
        } catch (e: Exception) {

            Log.e("PinCryptoManager", "Decryption failed: ${e.message}", e)
            null
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
        // 1. Validate old PIN
        val oldKeyBytes = getRawKeyIfPinValid(oldPin) ?: return false
        val oldKeyHex = oldKeyBytes.joinToString("") { "%02x".format(it) }

        // 2. Derive new key
        val newSalt = generateSalt()
        val newKey = deriveKeyFromPin(newPin, newSalt)
        val newKeyBytes = newKey.encoded
        val newKeyHex = newKeyBytes.joinToString("") { "%02x".format(it) }

        return try {
            SQLiteDatabase.loadLibs(context)

            // 3. Rekey using hex format for consistency
            SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath("encrypted_history.db").absolutePath,
                oldKeyHex,  // Using hex format here
                null,
                null
            ).use { db ->
                db.rawQuery("PRAGMA rekey = '$newKeyHex'", null).close()
            }

            // 4. Verify with new key (using hex)
            SQLiteDatabase.openOrCreateDatabase(
                context.getDatabasePath("encrypted_history.db").absolutePath,
                newKeyHex,
                null,
                null
            ).use { db ->
                db.rawQuery("SELECT 1", null).close()
            }

            // 5. Update stored credentials
            val secret = authSecret.toByteArray()
            val (encryptedSecret, iv) = encryptSecret(secret, newKey)

            context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE).edit {
                putString("salt", Base64.encodeToString(newSalt, Base64.NO_WRAP))
                putString("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
                putString("secret", Base64.encodeToString(encryptedSecret, Base64.NO_WRAP))
            }

            // 6. Refresh database instance
            DatabaseProvider.clearDatabaseInstance()
            true
        } catch (e: Exception) {
            Log.e("PinCryptoManager", "PIN change failed", e)
            false
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

    /**
     * Encrypts a secret value using AES in GCM mode with the provided key.
     *
     * @param secret The secret value to encrypt.
     * @param key The SecretKey to use for encryption.
     * @return A Pair containing the encrypted data and the IV used for encryption.
     */

    private fun encryptSecret(secret: ByteArray, key: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        val encrypted = cipher.doFinal(secret)
        return Pair(encrypted, iv)
    }

    /**
     * Decrypts an encrypted secret value using AES in GCM mode with the provided key and IV.
     *
     * @param encrypted The encrypted data to decrypt.
     * @param iv The IV used for decryption.
     * @param key The SecretKey to use for decryption.
     * @return The decrypted secret as a String, or null if decryption fails.
     */

    private fun decryptSecret(encrypted: ByteArray, iv: ByteArray, key: SecretKey): String? {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            val decrypted = cipher.doFinal(encrypted)
            String(decrypted)
        } catch (e: Exception) {
            Log.e("PinCryptoManager", "Decryption failed: ${e.message}", e)
            null
        }
    }
}