package com.example.cryptography.interfaces

import com.example.cryptography.data.CryptoParams
import javax.crypto.SecretKey

/**
 * SymmetricAlgorithm interface defines the methods for symmetric encryption and decryption operations.
 * It includes methods for generating keys and initialization vectors (IVs) for various algorithms.
 */
interface SymmetricAlgorithm {
    fun encrypt(params: CryptoParams): String
    fun decrypt(params: CryptoParams): String

    fun generateKey(algorithm: String, keySize: Int): SecretKey
    fun generateIV(algorithm: String, ivsize: Int): ByteArray
}
