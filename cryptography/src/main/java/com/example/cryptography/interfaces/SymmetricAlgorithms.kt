package com.example.cryptography.interfaces

import com.example.cryptography.data.CryptoParams
import javax.crypto.SecretKey

interface SymmetricAlgorithm {
    fun encrypt(params: CryptoParams): String
    fun decrypt(params: CryptoParams): String

    fun generateKey(algorithm: String, keySize: Int): SecretKey
    fun generateIV(algorithm: String, ivsize: Int): ByteArray
}
