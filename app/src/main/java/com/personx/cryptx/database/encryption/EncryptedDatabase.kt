package com.personx.cryptx.database.encryption

import androidx.room.Database
import androidx.room.RoomDatabase

/* * EncryptedDatabase is the Room database class that holds the encryption and decryption history.
 * It defines the entities and provides an abstract method to access the DAO.
 */

@Database(entities = [
    EncryptionHistory::class,
    DecryptionHistory::class,
    KeyPairHistory::class,
 ], version = 2, exportSchema = false)
abstract class EncryptedDatabase : RoomDatabase() {
    abstract fun historyDao(): EncryptionHistoryDao
    abstract fun keyPairDao(): KeyPairDao
}