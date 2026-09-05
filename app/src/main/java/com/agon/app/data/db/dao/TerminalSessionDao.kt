package com.agon.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agon.app.data.db.entity.TerminalSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TerminalSessionDao {
    @Query("SELECT * FROM terminal_sessions WHERE sandboxId = :sandboxId ORDER BY lastActiveAt DESC")
    fun observeBySandbox(sandboxId: Long): Flow<List<TerminalSessionEntity>>

    @Query("SELECT * FROM terminal_sessions WHERE id = :id")
    suspend fun getById(id: Long): TerminalSessionEntity?

    @Query("SELECT * FROM terminal_sessions WHERE id = :id")
    fun observeById(id: Long): Flow<TerminalSessionEntity?>

    @Query("SELECT * FROM terminal_sessions WHERE isActive = 1 ORDER BY lastActiveAt DESC")
    fun observeActive(): Flow<List<TerminalSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: TerminalSessionEntity): Long

    @Update
    suspend fun update(entity: TerminalSessionEntity)

    @Query("UPDATE terminal_sessions SET isActive = 0, lastActiveAt = :ts WHERE id = :id")
    suspend fun deactivate(id: Long, ts: Long = System.currentTimeMillis())

    @Query("UPDATE terminal_sessions SET currentWorkingDirectory = :cwd, lastActiveAt = :ts WHERE id = :id")
    suspend fun updateCwd(id: Long, cwd: String, ts: Long = System.currentTimeMillis())

    @Query("UPDATE terminal_sessions SET envVariables = :envJson, lastActiveAt = :ts WHERE id = :id")
    suspend fun updateEnv(id: Long, envJson: String, ts: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM terminal_sessions WHERE sandboxId = :sandboxId AND isActive = 1")
    suspend fun countActiveBySandbox(sandboxId: Long): Int

    @Query("DELETE FROM terminal_sessions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
