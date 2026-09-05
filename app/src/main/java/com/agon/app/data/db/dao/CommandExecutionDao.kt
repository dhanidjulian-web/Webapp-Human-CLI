package com.agon.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agon.app.data.db.entity.CommandExecutionEntity
import com.agon.app.data.model.CommandStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandExecutionDao {
    @Query("SELECT * FROM command_executions WHERE sessionId = :sessionId ORDER BY timestamp DESC")
    fun observeBySession(sessionId: Long): Flow<List<CommandExecutionEntity>>

    @Query("SELECT * FROM command_executions WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun observeBySessionAsc(sessionId: Long): Flow<List<CommandExecutionEntity>>

    @Query("SELECT * FROM command_executions ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<CommandExecutionEntity>>

    @Query("SELECT * FROM command_executions WHERE id = :id")
    suspend fun getById(id: Long): CommandExecutionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: CommandExecutionEntity): Long

    @Update
    suspend fun update(entity: CommandExecutionEntity)

    @Query(
        """
        UPDATE command_executions SET
            status = :status,
            exitCode = :exitCode,
            durationMs = :durationMs,
            stdoutPath = :stdoutPath,
            stderrPath = :stderrPath,
            timedOut = :timedOut
        WHERE id = :id
        """,
    )
    suspend fun complete(
        id: Long,
        status: CommandStatus,
        exitCode: Int?,
        durationMs: Long,
        stdoutPath: String?,
        stderrPath: String?,
        timedOut: Boolean,
    )

    @Query("UPDATE command_executions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: CommandStatus)

    @Query("SELECT COUNT(*) FROM command_executions WHERE status = :status")
    suspend fun countByStatus(status: CommandStatus): Int

    @Query("DELETE FROM command_executions WHERE sessionId = :sessionId")
    suspend fun deleteBySession(sessionId: Long)
}
