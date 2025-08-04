package com.personx.cryptx.database.encryption

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "key_pairs")
data class KeyPairHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "Untitled",
    val publicKey: String,
    val privateKey: String,
    val timestamp: Long = System.currentTimeMillis()
)
