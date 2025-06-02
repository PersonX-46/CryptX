package com.personx.cryptx.database.encryption

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EncryptionHistory::class], version = 1)
abstract class EncryptedDatabase : RoomDatabase() {
    abstract fun historyDao(): EncryptionHistoryDao
}