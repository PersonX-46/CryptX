package com.personx.cryptx.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    fun generateSecretKey(algorithm: String, keySize: Int): SecretKey {
        val keyGen = KeyGenerator.getInstance(algorithm)
        keyGen.init(keySize)
        return keyGen.generateKey()
    }

    fun encodeByteArrayToString(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun decodeBase64ToSecretKey(base64Key: String, algorithm: String): SecretKey {
        val decodedKey = Base64.decode(base64Key, Base64.NO_WRAP)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, algorithm)
    }

    fun decodeBase64ToIV(base64IV: String): ByteArray {
        return Base64.decode(base64IV, Base64.DEFAULT)
    }

    fun padTextToBlockSize(input: String, blockSize: Int): ByteArray {
        val inputBytes = input.toByteArray()
        val paddingLength = blockSize - (inputBytes.size % blockSize)
        return inputBytes + ByteArray(paddingLength) { paddingLength.toByte() }
    }

    fun hexStringToByteArray(hex: String): ByteArray {
        val result = ByteArray(hex.length / 2)
        for (i in result.indices) {
            val index = i * 2
            val byte = hex.substring(index, index + 2).toInt(16)
            result[i] = byte.toByte()
        }
        return result
    }

    fun byteArrayToHexString(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun generateRandomIV(size: Int): ByteArray {
        val iv = ByteArray(size)
        SecureRandom().nextBytes(iv)
        return iv
    }

}