package com.agon.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agon.app.data.db.entity.SandboxEntity
import com.agon.app.data.model.SandboxStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SandboxDao {
    @Query("SELECT * FROM sandboxes ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<SandboxEntity>>

    @Query("SELECT * FROM sandboxes WHERE id = :id")
    fun observeById(id: Long): Flow<SandboxEntity?>

    @Query("SELECT * FROM sandboxes WHERE id = :id")
    suspend fun getById(id: Long): SandboxEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: SandboxEntity): Long

    @Update
    suspend fun update(entity: SandboxEntity)

    @Delete
    suspend fun delete(entity: SandboxEntity)

    @Query("DELETE FROM sandboxes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE sandboxes SET status = :status, lastError = :error, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: SandboxStatus, error: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE sandboxes SET activeSessionCount = :count, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateSessionCount(id: Long, count: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM sandboxes")
    suspend fun count(): Int
}
