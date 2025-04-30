package com.personx.cryptx.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    fun generateSecretKey(algorithm: String, keySize: Int): SecretKey {
        val keyGen = KeyGenerator.getInstance(algorithm)
        when (algorithm) {
            "DES" -> keyGen.init(56) // DES only supports 56-bit keys (but uses 64 bits incl. parity)
            "3DES" -> keyGen.init(168)
            else -> keyGen.init(keySize)
        }
        return keyGen.generateKey()
    }

//    fun secretKeyToHex(secretKey: SecretKey): String {
//        val keyBytes = secretKey.encoded
//        return keyBytes.joinToString("") { "%02x".format(it) }
//    }
//
//    fun hexToSecretKey(hex: String, algorithm: String): SecretKey {
//        val bytes = hex.chunked(2)
//            .map { it.toInt(16).toByte() }
//            .toByteArray()
//
//        return SecretKeySpec(bytes, algorithm)
//    }

    fun decodeBase64ToSecretKey(keyString: String, algorithm: String): SecretKey {
        val decodedKey = Base64.decode(keyString, Base64.NO_WRAP)
        val keyBytes = when (algorithm) {
            "DES" -> decodedKey.copyOf(8) // truncate or pad to 8 bytes
            "3DES" -> decodedKey.copyOf(24) // 3DES requires 24 bytes
            else -> decodedKey
        }
        return SecretKeySpec(keyBytes, algorithm)
    }

    fun encodeByteArrayToString(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    fun decodeStringToByteArray(base64IV: String): ByteArray {
        return Base64.decode(base64IV, Base64.NO_WRAP)
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