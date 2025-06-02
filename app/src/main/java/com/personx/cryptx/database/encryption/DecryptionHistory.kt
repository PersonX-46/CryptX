package com.personx.cryptx.database.encryption

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "decryption_history")
data class DecryptionHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val algorithm: String,
    val transformation: String,
    val key: String,
    val iv: String?,
    val encryptedText: String,
    val decryptedOutput: String,
    val timestamp: Long = System.currentTimeMillis()
)