package com.agon.app.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.agon.app.data.model.ShellType

@Entity(
    tableName = "terminal_sessions",
    foreignKeys = [
        ForeignKey(
            entity = SandboxEntity::class,
            parentColumns = ["id"],
            childColumns = ["sandboxId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sandboxId")],
)
data class TerminalSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sandboxId: Long,
    val shellType: ShellType = ShellType.SH,
    val currentWorkingDirectory: String,
    /** Serialized JSON map of environment variables. */
    val envVariables: String = "{}",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
)
