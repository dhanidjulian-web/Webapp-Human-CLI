package com.agon.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.agon.app.data.model.CommandClassification

@Entity(
    tableName = "command_policies",
    indices = [Index(value = ["commandPattern"], unique = true)],
)
data class CommandPolicyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val commandPattern: String,
    val classification: CommandClassification,
    val description: String = "",
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
)
