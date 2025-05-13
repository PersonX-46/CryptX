package com.example.cryptography.utils

import java.security.MessageDigest
import java.util.regex.Pattern

object HashUtils {
    private val hashPatterns = mapOf(
        "MD5" to "^[a-f0-9]{32}$",
        "SHA-1" to "^[a-f0-9]{40}$",
        "SHA-256" to "^[a-f0-9]{64}$",
        "SHA-384" to "^[a-f0-9]{96}$",
        "SHA-512" to "^[a-f0-9]{128}$",
        "CRC32" to "^[a-f0-9]{8}$",
        "MySQL 4.1+" to "^\\*[a-f0-9]{40}$",
        "NTLM" to "^[a-f0-9]{32}$", // Same length as MD5 but different usage
        "bcrypt" to "^\\\$2[aby]?\\\$\\d{2}\\\$[./0-9A-Za-z]{53}$",
        "Argon2" to "^\\\$argon2[id]?\\\$v=\\d+\\\$m=\\d+,t=\\d+,p=\\d+\\$[a-zA-Z0-9+/]+\\\$[a-zA-Z0-9+/]+$",
        "SHA-3-256" to "^[a-f0-9]{64}$", // Same length as SHA-256
        "SHA-3-512" to "^[a-f0-9]{128}$" // Same length as SHA-512
    )

    private val hashLengths = mapOf(
        32 to listOf("MD5", "NTLM"),
        40 to listOf("SHA-1", "MySQL 4.1+"),
        64 to listOf("SHA-256", "SHA-3-256"),
        96 to listOf("SHA-384"),
        128 to listOf("SHA-512", "SHA-3-512"),
        8 to listOf("CRC32")
    )

    /**
     * Identifies possible hash types based on the input string
     * @param hash The hash string to identify
     * @return List of possible hash algorithms
     */
    fun identifyHash(hash: String): List<String> {
        val possibleHashes = mutableListOf<String>()

        // Check for special patterns first (like bcrypt, Argon2)
        for ((algorithm, pattern) in hashPatterns) {
            if (Pattern.matches(pattern, hash)) {
                possibleHashes.add(algorithm)
            }
        }

        // If no special pattern matched, check by length and charset
        if (possibleHashes.isEmpty()) {
            val length = hash.length
            val isHex = hash.matches(Regex("^[a-fA-F0-9]+\$"))

            if (isHex && hashLengths.containsKey(length)) {
                possibleHashes.addAll(hashLengths[length]!!)
            } else {
                possibleHashes.add("Unknown hash type")
            }
        }

        return possibleHashes.distinct()
    }

    /**
     * Provides additional information about a hash algorithm
     * @param algorithm The algorithm name
     * @return Information string about the algorithm
     */
    fun getHashInfo(algorithm: String): String {
        return when (algorithm.uppercase()) {
            "MD5" -> "MD5: 128-bit hash, widely used but considered cryptographically broken"
            "SHA-1" -> "SHA-1: 160-bit hash, no longer considered secure against well-funded attackers"
            "SHA-256" -> "SHA-256: 256-bit hash, part of SHA-2 family, widely used and secure"
            "SHA-384" -> "SHA-384: 384-bit hash, part of SHA-2 family"
            "SHA-512" -> "SHA-512: 512-bit hash, part of SHA-2 family"
            "CRC32" -> "CRC32: 32-bit checksum, not cryptographic, used for error detection"
            "MYSQL 4.1+" -> "MySQL 4.1+: SHA-1 based password hash with salt, preceded by *"
            "NTLM" -> "NTLM: Microsoft's authentication protocol hash"
            "BCRYPT" -> "bcrypt: Adaptive hash function based on Blowfish, designed for password hashing"
            "ARGON2" -> "Argon2: Winner of PHC, modern password hashing algorithm"
            "SHA-3-256" -> "SHA-3-256: 256-bit hash, part of SHA-3 (Keccak) family"
            "SHA-3-512" -> "SHA-3-512: 512-bit hash, part of SHA-3 (Keccak) family"
            else -> "No information available about this hash algorithm"
        }
    }

    fun computeHash(input: String, algorithm: String): String {
        return try {
            val digest = MessageDigest.getInstance(algorithm)
            val hashBytes = digest.digest(input.toByteArray())
            hashBytes.joinToString("") { "%02x".format(it) } // Convert bytes to hex
        } catch (e: Exception) {
            "Error: ${e.message}" // Handle unsupported algorithms
        }
    }
}