package com.agon.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.agon.app.data.model.CommandStatus

@Entity(
    tableName = "command_executions",
    foreignKeys = [
        ForeignKey(
            entity = TerminalSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId"), Index("timestamp")],
)
data class CommandExecutionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val commandText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long = 0,
    val exitCode: Int? = null,
    val stdoutPath: String? = null,
    val stderrPath: String? = null,
    val status: CommandStatus = CommandStatus.QUEUED,
    val classification: String? = null,
    val timedOut: Boolean = false,
)
