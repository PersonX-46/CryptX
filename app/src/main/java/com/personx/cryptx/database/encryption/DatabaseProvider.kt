package com.personx.cryptx.database.encryption

import android.content.Context
import androidx.room.Room
import com.personx.cryptx.crypto.PinCryptoManager
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

object DatabaseProvider {

    @Volatile
    private var INSTANCE: EncryptedDatabase? = null

    fun getDatabase(context: Context, pin: String): EncryptedDatabase? {
        synchronized(this) {
            if (INSTANCE != null) return INSTANCE

            val pinCryptoManager = PinCryptoManager(context)
            val keyBytes = pinCryptoManager.getRawKeyIfPinValid(pin)

            if (keyBytes == null) {
                return null // Invalid PIN, cannot access database
            }

            SQLiteDatabase.loadLibs(context)

            val factory = SupportFactory(keyBytes)

            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                EncryptedDatabase::class.java,
                "encrypted_history.db"
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration(false)
                .build()

            return INSTANCE
        }
    }
}