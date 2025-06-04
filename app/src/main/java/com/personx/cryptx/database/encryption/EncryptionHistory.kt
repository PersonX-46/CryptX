package com.personx.cryptx.database.encryption

import androidx.room.Entity
import androidx.room.PrimaryKey

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
