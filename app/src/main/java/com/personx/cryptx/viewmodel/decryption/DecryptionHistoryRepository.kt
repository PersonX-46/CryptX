package com.personx.cryptx.viewmodel.decryption

import android.content.Context
import com.personx.cryptx.database.encryption.DatabaseProvider
import com.personx.cryptx.database.encryption.DecryptionHistory
import com.personx.cryptx.database.encryption.EncryptedDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/* * DecryptionHistoryRepository is responsible for managing the decryption history in the database.
 * It provides methods to insert new decryption records and retrieve all decryption history.
 * The repository ensures that the database instance is initialized with the correct encryption key (PIN).
 */

class DecryptionHistoryRepository(private val context: Context) {
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
        // If the current PIN is null or does not match the provided PIN, reinitialize the database
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
     * Inserts a new decryption history record into the database.
     * If the database initialization fails (e.g., invalid PIN), it returns false.
     *
     * @param pin The PIN to use for accessing the database.
     * @param history The DecryptionHistory object to be inserted.
     * @return True if the insertion was successful, false otherwise.
     */

    suspend fun insertHistory(pin: String, history: DecryptionHistory): Boolean {
        // Ensure the database is initialized with the provided PIN
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

    /**
     * Retrieves all decryption history records from the database.
     * It returns a Flow that emits a list of DecryptionHistory objects.
     * If the database initialization fails (e.g., invalid PIN), it emits an empty list.
     *
     * @param pin The PIN to use for accessing the database.
     * @return A Flow that emits a list of DecryptionHistory objects.
     */

    fun getAllDecryptionHistory(pin: String): Flow<List<DecryptionHistory>> {
        // Use flow builder to emit the decryption history records
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