package com.personx.cryptx.database.encryption

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface EncryptionHistoryDao {


    /** Inserts a new encryption history record into the database.
     * If a record with the same primary key already exists, it will be replaced.
     *
     * @param history The EncryptionHistory object to be inserted.
     */

    @Insert
    suspend fun insertEncryptionHistory(history: EncryptionHistory)

    /**
     * Retrieves all encryption history records from the database, ordered by timestamp in descending order.
     *
     * @return A Flow that emits a list of EncryptionHistory objects.
     */

    @Query("SELECT * FROM encryption_history ORDER BY timestamp DESC")
    fun getAllEncryptionHistory(): Flow<List<EncryptionHistory>>

    /**
     * Retrieves the count of all encryption history records in the database.
     *
     * @return The count of encryption history records.
     */

    @Query("SELECT COUNT(*) FROM encryption_history")
    suspend fun getCount(): Int

    /**
     * Updates an existing encryption history record in the database.
     * The record must already exist; otherwise, it will not be inserted.
     *
     * @param history The EncryptionHistory object to be updated.
     */

    @Update
    suspend fun updateEncryptionHistory(history: EncryptionHistory)

    /**
     * Deletes a specific encryption history record from the database.
     *
     * @param history The EncryptionHistory object to be deleted.
     */

    @Delete
    suspend fun deleteEncryptionHistory(history: EncryptionHistory)

    /**
     * Inserts a new decryption history record into the database.
     * If a record with the same primary key already exists, it will be replaced.
     *
     * @param history The DecryptionHistory object to be inserted.
     */

    @Insert
    suspend fun insertDecryptionHistory(history: DecryptionHistory)

    /**
     * Retrieves all decryption history records from the database, ordered by timestamp in descending order.
     *
     * @return A Flow that emits a list of DecryptionHistory objects.
     */

    @Query("SELECT * FROM decryption_history ORDER BY timestamp DESC")
    fun getAllDecryptionHistory(): Flow<List<DecryptionHistory>>

    @Update
    /**
     * Updates an existing decryption history record in the database.
     * The record must already exist; otherwise, it will not be inserted.
     *
     * @param history The DecryptionHistory object to be updated.
     */
    suspend fun updateDecryptionHistory(history: DecryptionHistory)

    /**
     * Deletes a specific decryption history record from the database.
     *
     * @param history The DecryptionHistory object to be deleted.
     */

    @Delete
    suspend fun deleteDecryptionHistory(history: DecryptionHistory)


}

@Dao
interface KeyPairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pair: KeyPairHistory)

    @Query("SELECT * FROM key_pairs ORDER BY timestamp DESC")
    fun getAllPairs(): Flow<List<KeyPairHistory>>

    @Update
    suspend fun updateKeyPair(history: KeyPairHistory)

    @Delete
    suspend fun deleteKeyPair(history: KeyPairHistory)
}