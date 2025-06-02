package com.personx.cryptx.crypto

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class PinCryptoManager(private val context: Context) {

    fun setupPin(pin: String) {
        val salt = generateSalt()
        val key = deriveKeyFromPin(pin, salt)
        val secret = "auth_secret_salt".toByteArray()
        val (encryptedSecret, iv) = encryptSecret(secret, key)

        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        prefs.edit {
            putString("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
            putString("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
            putString("secret", Base64.encodeToString(encryptedSecret, Base64.NO_WRAP))
        }
    }

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
            return decryptedSecret == "auth_secret_salt" // Check against a known value
        } catch (e: Exception) {
            false
        }
    }

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
            if (decryptedSecret == "auth_secret_salt") {
                key.encoded // return raw key bytes
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    private fun deriveKeyFromPin(pin: String, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(pin.toCharArray(), salt, 10000, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun generateSalt(): ByteArray {
        val secret = ByteArray(16)
        SecureRandom().nextBytes(secret)
        return secret
    }

    private fun encryptSecret(secret: ByteArray, key: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        val encrypted = cipher.doFinal(secret)
        return Pair(encrypted, iv)
    }

    private fun decryptSecret(encrypted: ByteArray, iv: ByteArray, key: SecretKey): String? {
        return try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            val decrypted = cipher.doFinal(encrypted)
            String(decrypted)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}