package com.personx.cryptx.database.encryption

import android.service.autofill.OnClickAction
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface EncryptionHistoryDao {
    /**
     * Inserts a new encryption history record into the database.
     * If a record with the same primary key already exists, it will be replaced.
     *
     * @param history The EncryptionHistory object to be inserted.
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEncryptionHistory(history: EncryptionHistory)

    /**
     * Retrieves all encryption history records from the database, ordered by timestamp in descending order.
     *
     * @return A Flow that emits a list of EncryptionHistory objects.
     */
    @Query("SELECT * FROM encryption_history ORDER BY timestamp DESC")
    fun getAllEncryptionHistory(): Flow<List<EncryptionHistory>>


    /**
     * Inserts a new decryption history record into the database.
     * If a record with the same primary key already exists, it will be replaced.
     *
     * @param history The DecryptionHistory object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecryptionHistory(history: DecryptionHistory)

    /**
     * Retrieves all decryption history records from the database, ordered by timestamp in descending order.
     *
     * @return A Flow that emits a list of DecryptionHistory objects.
     */
    @Query("SELECT * FROM decryption_history ORDER BY timestamp DESC")
    fun getAllDecryptionHistory(): Flow<List<DecryptionHistory>>


}