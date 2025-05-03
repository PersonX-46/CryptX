package com.example.cryptography.data

import javax.crypto.SecretKey

data class CryptoParams(
    val data: String,
    val key: SecretKey,
    val transformation: String,
    val iv: ByteArray? = null, // Optional
    val useBase64: Boolean = true // New: should we output in Base64?
)
