package com.personx.cryptx.viewmodel.encryption

import android.content.Context
import android.util.Log
import com.personx.cryptx.database.encryption.DatabaseProvider
import com.personx.cryptx.database.encryption.EncryptedDatabase
import com.personx.cryptx.database.encryption.EncryptionHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EncryptionHistoryRepository(private val context: Context) {
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

    suspend fun insertHistory(pin: String, history: EncryptionHistory): Boolean {
        return try {
            val db = ensureDatabase(pin) ?: return false
            db.historyDao().insertEncryptionHistory(history)
            true
        } catch (e: Exception) {
            Log.e("DB_INSERT", "Failed to insert", e)
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }

    fun getAllHistory(pin: String): Flow<List<EncryptionHistory>> {
        return flow {
            try {
                val db = ensureDatabase(pin) ?: throw Exception("DB init failed")
                db.historyDao().getAllEncryptionHistory().collect { emit(it) }
            } catch (e: Exception) {
                Log.e("DB_FETCH", "Error fetching", e)
                emit(emptyList())
            } finally {
                DatabaseProvider.clearDatabaseInstance()
            }
        }
    }



    // Debug function
    suspend fun debugDatabase(pin: String) {
        val db = ensureDatabase(pin) ?: return
        val count = db.historyDao().getCount()
        Log.d("DB_DEBUG", "Total records: $count")
    }
}
