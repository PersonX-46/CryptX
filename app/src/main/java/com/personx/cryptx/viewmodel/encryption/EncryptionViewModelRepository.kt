package com.personx.cryptx.viewmodel.encryption

import android.content.Context
import com.personx.cryptx.database.encryption.DatabaseProvider
import com.personx.cryptx.database.encryption.EncryptedDatabase
import com.personx.cryptx.database.encryption.EncryptionHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EncryptionViewModelRepository(private val context: Context) {

    /**
     * EncryptionViewModelRepository is responsible for managing the encryption history in the database.
     * It provides methods to insert new encryption records and retrieve all encryption history.
     * The repository ensures that the database instance is initialized with the correct encryption key (PIN).
     */


    private var currentPin: String? = null


    /**
     * Ensures that the database is initialized with the provided PIN.
     * If the current PIN matches the provided PIN, it returns the existing database instance.
     * If not, it clears the existing instance and creates a new one with the new PIN.
     *
     * @param pin The PIN to use for accessing the database.
     * @return An instance of EncryptedDatabase or null if the PIN is invalid.
     */

    private fun ensureDatabase(pin: String): EncryptedDatabase? {
        return if (currentPin == pin) {
            DatabaseProvider.getDatabase(context, pin)
        } else {
            DatabaseProvider.clearDatabaseInstance()
            DatabaseProvider.getDatabase(context, pin).also {
                currentPin = pin
            }
        }
    }

    /**
     * Inserts a new encryption history record into the database.
     * If the database initialization fails (e.g., invalid PIN), it returns false.
     *
     * @param pin The PIN to use for accessing the database.
     * @param history The EncryptionHistory object to be inserted.
     * @return True if the insertion was successful, false otherwise.
     */

    suspend fun insertHistory(pin: String, history: EncryptionHistory): Boolean {
        return try {
            val db = ensureDatabase(pin) ?: return false
            db.historyDao().insertEncryptionHistory(history)
            true
        } catch (e: Exception) {
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }

    /**
     * Retrieves all encryption history records from the database.
     * If the database initialization fails (e.g., invalid PIN), it returns an empty list.
     *
     * @param pin The PIN to use for accessing the database.
     * @return A Flow that emits a list of EncryptionHistory objects.
     */

    fun getAllHistory(pin: String): Flow<List<EncryptionHistory>> {
        return flow {
            try {
                val db = ensureDatabase(pin) ?: throw Exception("DB init failed")
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
     * @param pin The PIN to use for accessing the database.
     * @param history The EncryptionHistory object to be updated.
     * @return True if the update was successful, false otherwise.
     */

    suspend fun updateHistory(pin: String, history: EncryptionHistory): Boolean {
        return try {
            val db = ensureDatabase(pin) ?: return false
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
     * @param pin The PIN to use for accessing the database.
     * @param history The EncryptionHistory object to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */

    suspend fun deleteHistory(pin: String, history: EncryptionHistory): Boolean {
        return try {
            val db = ensureDatabase(pin) ?: return false
            db.historyDao().deleteEncryptionHistory(history)
            true
        } catch (e: Exception) {
            false
        } finally {
            DatabaseProvider.clearDatabaseInstance()
        }
    }

    /**
     * Debug function to check the database count.
     * This is for internal use and should not be exposed in production code.
     *
     * @param pin The PIN to use for accessing the database.
     */

    // Debug function
    suspend fun debugDatabase(pin: String) {
        val db = ensureDatabase(pin) ?: return
        val count = db.historyDao().getCount()
    }
}
