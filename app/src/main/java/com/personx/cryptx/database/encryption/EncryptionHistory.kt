package com.personx.cryptx.database.encryption

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DecryptionHistory represents a record of a decryption operation in the database.
 * It includes details such as the algorithm used, transformation, key, IV, encrypted text,
 * whether the text was base64 encoded, the decrypted output, and a timestamp.
 */

@Entity(tableName = "encryption_history")
data class EncryptionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val algorithm: String,
    val transformation: String,
    val keySize: Int,
    val key: String,
    val iv: String?,
    val secretText: String,
    val isBase64: Boolean,
    val encryptedOutput: String,
    val timestamp: Long = System.currentTimeMillis()
)
