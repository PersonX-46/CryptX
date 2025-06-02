package com.personx.cryptx.database.encryption

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
    suspend fun insertHistory(history: EncryptionHistory)

    /**
     * Retrieves all encryption history records from the database, ordered by timestamp in descending order.
     *
     * @return A Flow that emits a list of EncryptionHistory objects.
     */
    @Query("SELECT * FROM encryption_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<EncryptionHistory>>


}