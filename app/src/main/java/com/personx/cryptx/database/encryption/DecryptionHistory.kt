package com.personx.cryptx.database.encryption

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DecryptionHistory represents a record of a decryption operation in the database.
 * It includes details such as the algorithm used, transformation, key, IV, encrypted text,
 * whether the text was base64 encoded, the decrypted output, and a timestamp.
 */

@Entity(tableName = "decryption_history")
data class DecryptionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val algorithm: String,
    val transformation: String,
    val key: String,
    val iv: String?,
    val encryptedText: String,
    val isBase64: Boolean,
    val decryptedOutput: String,
    val timestamp: Long = System.currentTimeMillis()
)