package com.example.cryptography.algorithms

import android.util.Base64
import com.example.cryptography.data.CryptoParams
import com.example.cryptography.interfaces.SymmetricAlgorithm
import com.example.cryptography.utils.CryptoUtils.byteArrayToHexString
import com.example.cryptography.utils.CryptoUtils.encodeByteArrayToString
import com.example.cryptography.utils.CryptoUtils.hexStringToByteArray
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class SymmetricBasedAlgorithm: SymmetricAlgorithm {

    override fun encrypt(params: CryptoParams): String {
        val cipher = Cipher.getInstance(params.transformation)
        if (params.iv == null || params.iv.isEmpty()) {
            cipher.init(Cipher.ENCRYPT_MODE, params.key)
        } else {
            val ivSpec = IvParameterSpec(params.iv)
            cipher.init(Cipher.ENCRYPT_MODE, params.key, ivSpec)
        }

        val encryptedBytes = cipher.doFinal(params.data.toByteArray())

        return if (params.useBase64) {
            encodeByteArrayToString(encryptedBytes)
        } else  {
            byteArrayToHexString(encryptedBytes)
        }
    }

    override fun decrypt(params: CryptoParams): String {
        val cipher = Cipher.getInstance(params.transformation)
        val ivSpec = params.iv?.let { IvParameterSpec(it) }

        cipher.init(Cipher.DECRYPT_MODE, params.key, ivSpec)

        val encryptedBytes = if (params.useBase64) {
            Base64.decode(params.data, Base64.DEFAULT)
        } else {
            hexStringToByteArray(params.data)
        }

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }

    override fun generateKey(algorithm: String, keySize: Int): SecretKey {
        val keyGen = KeyGenerator.getInstance(algorithm)
        when (algorithm) {
            "DES" -> keyGen.init(56) // DES only supports 56-bit keys (but uses 64 bits incl. parity)
            "DESede" -> keyGen.init(168)
            else -> keyGen.init(keySize)
        }
        return keyGen.generateKey()
    }

    override fun generateIV(algorithm: String, ivsize: Int): ByteArray {
        val ivSize = when (algorithm.uppercase()) {
            "AES", "AES/CBC/PKCS5PADDING", "AES/CBC/PKCS7PADDING", "AES/GCM/NO PADDING", "AES/GCM/NOPADDING" -> 16
            "CHACHA20", "CHACHA20-POLY1305" -> 12
            "DES" -> 8
            "DESEDE"-> 8
            "BLOWFISH" -> 8
            else -> ivsize
        }

        val iv = ByteArray(ivSize)
        SecureRandom().nextBytes(iv)
        return iv
    }

}