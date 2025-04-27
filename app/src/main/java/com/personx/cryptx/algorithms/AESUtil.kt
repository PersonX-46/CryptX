package com.personx.cryptx.algorithms

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object AESUtil {
    // Generate a secure AES key (256-bit)
    // 🧰 Make a random secret key (256-bit)
    fun generateSecretKey(keySize: Int): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(keySize)
        return keyGen.generateKey()
    }

    // 🧪 Encrypt the message using AES + IV
    fun encrypt(plainText: String, key: SecretKey, iv: ByteArray): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(iv)

        // 🔐 Lock the message with key and IV
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())

        // 📦 Convert the locked box into a Base64 string (easy to print!)
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    // 🧪 Decrypt the message using the same key + IV
    fun decrypt(encryptedText: String, key: SecretKey, iv: ByteArray): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(iv)

        // 🔓 Unlock the message using the key + IV
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        val decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)

        // 📜 Return the original message
        return String(decryptedBytes)
    }



}