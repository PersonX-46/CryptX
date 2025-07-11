package com.personx.cryptx.viewmodel.encryption

import android.content.Context
import android.util.Log
import com.personx.cryptx.database.encryption.DatabaseProvider
import com.personx.cryptx.database.encryption.EncryptedDatabase
import com.personx.cryptx.database.encryption.EncryptionHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 * EncryptionViewModelRepository is responsible for managing the encryption history in the database.
 * It provides methods to insert new encryption records and retrieve all encryption history.
 * The repository ensures that the database instance is initialized with the correct encryption key (PIN).
 */
class EncryptionViewModelRepository(private val context: Context) {

    /**
     * Ensures that the database is initialized with the provided PIN.
     * If the current PIN matches the provided PIN, it returns the existing database instance.
     * If not, it clears the existing instance and creates a new one with the new PIN.
     *
     * @return An instance of EncryptedDatabase or null if the PIN is invalid.
     */

    private fun ensureDatabase(): EncryptedDatabase? {
        return DatabaseProvider.getDatabase(context)
    }

    /**
     * Inserts a new encryption history record into the database.
     * If the database initialization fails (e.g., invalid PIN), it returns false.
     *
     * @param history The EncryptionHistory object to be inserted.
     * @return True if the insertion was successful, false otherwise.
     */

    suspend fun insertHistory(history: EncryptionHistory): Boolean {
        return try {
            val db = ensureDatabase() ?: return false
            db.historyDao().insertEncryptionHistory(history)
            true
        } catch (e: Exception) {
            Log.d("EncryptionViewModelRepository", "Insert history failed: ${e.message}")
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }

    /**
     * Retrieves all encryption history records from the database.
     * If the database initialization fails (e.g., invalid PIN), it returns an empty list.
     *
     * @return A Flow that emits a list of EncryptionHistory objects.
     */

    fun getAllHistory(): Flow<List<EncryptionHistory>> {
        return flow {
            try {
                val db = ensureDatabase() ?: throw Exception("DB init failed")
                db.historyDao().getAllEncryptionHistory().collect { emit(it) }
            } catch (e: Exception) {
                emit(emptyList())
            } finally {
                DatabaseProvider.clearDatabaseInstance()
            }
        }
    }

    /**
     * Updates an existing encryption history record in the database.
     * If the database initialization fails (e.g., invalid PIN), it returns false.
     *
     * @param history The EncryptionHistory object to be updated.
     * @return True if the update was successful, false otherwise.
     */

    suspend fun updateHistory(history: EncryptionHistory): Boolean {
        return try {
            val db = ensureDatabase() ?: return false
            db.historyDao().updateEncryptionHistory(history)
            true
        } catch (e: Exception) {
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }

    /**
     * Deletes a specific encryption history record from the database.
     * If the database initialization fails (e.g., invalid PIN), it returns false.
     *
     * @param history The EncryptionHistory object to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */

    suspend fun deleteHistory(history: EncryptionHistory): Boolean {
        return try {
            val db = ensureDatabase() ?: return false
            db.historyDao().deleteEncryptionHistory(history)
            true
        } catch (e: Exception) {
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }
}
