package com.personx.cryptx.database.encryption

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.personx.cryptx.crypto.SessionKeyManager
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

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
     * @return An instance of EncryptedDatabase or null if the PIN is invalid.
     */

    fun getDatabase(context: Context): EncryptedDatabase? {
        synchronized(this) {
            if (INSTANCE != null) return INSTANCE

            val sessionKey = SessionKeyManager.getSessionKey() ?: return null

            System.loadLibrary("sqlcipher")
            val factory = SupportOpenHelperFactory(sessionKey.encoded)

            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                EncryptedDatabase::class.java,
                "encrypted_history.db"
            )
                .openHelperFactory(factory)
                .addMigrations(MIGRATION_1_2,
                    MIGRATION_2_3
                )
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