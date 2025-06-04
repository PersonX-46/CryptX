package com.personx.cryptx.viewmodel.decryption

import android.content.Context
import com.personx.cryptx.database.encryption.DatabaseProvider
import com.personx.cryptx.database.encryption.DecryptionHistory
import com.personx.cryptx.database.encryption.EncryptedDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DecryptionHistoryRepository(private val context: Context) {
    private var currentPin: String? = null

    suspend fun ensureDatabase(pin: String): EncryptedDatabase? {
        return if (currentPin == pin) {
            DatabaseProvider.getDatabase(context, pin)
        } else {
            DatabaseProvider.clearDatabaseInstance()
            DatabaseProvider.getDatabase(context, pin).also {
                currentPin = pin
            }
        }
    }

    suspend fun insertHistory(pin: String, history: DecryptionHistory): Boolean {
        return try {
            val db = ensureDatabase(pin) ?: return false
            db.historyDao().insertDecryptionHistory(history)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }

    fun getAllDecryptionHistory(pin: String): Flow<List<DecryptionHistory>> {
        return flow {
            try {
                val db = ensureDatabase(pin) ?: throw Exception("DB init failed")
                db.historyDao().getAllDecryptionHistory().collect { emit(it) }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(emptyList())
            } finally {
                DatabaseProvider.clearDatabaseInstance()
            }
        }
    }
}