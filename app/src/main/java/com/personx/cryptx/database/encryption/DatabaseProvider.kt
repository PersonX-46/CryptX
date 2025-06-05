package com.personx.cryptx.database.encryption

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.personx.cryptx.crypto.PinCryptoManager
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

object DatabaseProvider {

    /**
     * Singleton instance of the EncryptedDatabase.
     * This is initialized when the database is first accessed with a valid PIN.
     */

    @Volatile
    private var INSTANCE: EncryptedDatabase? = null

    /**
     * Returns an instance of the EncryptedDatabase, creating it if it doesn't already exist.
     * If the provided PIN is invalid, returns null.
     *
     * @param context The application context.
     * @param pin The PIN to validate and use for decryption.
     * @return An instance of EncryptedDatabase or null if the PIN is invalid.
     */

    fun getDatabase(context: Context, pin: String): EncryptedDatabase? {

        // Check if the database instance already exists
        synchronized(this) {

            // If the instance is already created, return it
            if (INSTANCE != null) return INSTANCE

            // If the instance is null, we need to create it
            val pinCryptoManager = PinCryptoManager(context)
            val keyBytes = pinCryptoManager.getRawKeyIfPinValid(pin)

            if (keyBytes == null) {
                return null // Invalid PIN, cannot access database
            }

            // Load the SQLCipher library
            SQLiteDatabase.loadLibs(context)

            // Create a SupportFactory with the key bytes
            val factory = SupportFactory(keyBytes)

            // Build the database instance using Room
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                EncryptedDatabase::class.java,
                "encrypted_history.db"
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration(false)
                .build()
                .also { INSTANCE = it }

            return INSTANCE
        }
    }

    /**
     * Clears the database instance, closing it if it is open.
     * This should be called when the application is shutting down or when the database is no longer needed.
     */

    @Synchronized
    fun clearDatabaseInstance() {
        INSTANCE?.let { db ->
            try {
                if (db.isOpen) {
                    db.close()
                } else {
                    Log.w("DB_CLOSE", "Database was already closed")
                }
            } catch (e: Exception) {
                Log.e("DB_CLOSE", "Error closing database", e)
            } finally {
                INSTANCE = null
            }
        }
    }
}