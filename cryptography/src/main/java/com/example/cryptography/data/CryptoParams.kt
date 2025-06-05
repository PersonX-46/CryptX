package com.example.cryptography.data

import javax.crypto.SecretKey

/**
 * CryptoParams holds the parameters required for cryptographic operations such as encryption and decryption.
 * It includes the data to be processed, the secret key, the transformation type, an optional IV (initialization vector),
 * and a flag indicating whether to output the result in Base64 format.
 */

data class CryptoParams(
    val data: String,
    val key: SecretKey,
    val transformation: String,
    val iv: ByteArray? = null, // Optional
    val useBase64: Boolean = true // New: should we output in Base64?
)
