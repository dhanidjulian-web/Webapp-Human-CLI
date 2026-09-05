package com.agon.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agon.app.data.db.entity.CredentialEntity
import com.agon.app.data.model.CredentialType
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDao {
    @Query("SELECT * FROM credentials ORDER BY type ASC, label ASC")
    fun observeAll(): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials WHERE id = :id")
    suspend fun getById(id: Long): CredentialEntity?

    @Query("SELECT * FROM credentials WHERE type = :type ORDER BY label ASC")
    fun observeByType(type: CredentialType): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials WHERE type = :type LIMIT 1")
    suspend fun getFirstByType(type: CredentialType): CredentialEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: CredentialEntity): Long

    @Update
    suspend fun update(entity: CredentialEntity)

    @Query("DELETE FROM credentials WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        UPDATE credentials SET
            lastValidatedAt = :validatedAt,
            isValid = :isValid,
            validationMessage = :message,
            updatedAt = :validatedAt
        WHERE id = :id
        """,
    )
    suspend fun updateValidation(
        id: Long,
        isValid: Boolean,
        message: String,
        validatedAt: Long = System.currentTimeMillis(),
    )

    @Query("SELECT COUNT(*) FROM credentials")
    suspend fun count(): Int
}
