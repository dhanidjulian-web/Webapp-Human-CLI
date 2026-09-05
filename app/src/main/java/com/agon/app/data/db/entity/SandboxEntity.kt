package com.agon.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.agon.app.data.model.LinuxDistro
import com.agon.app.data.model.SandboxStatus

@Entity(
    tableName = "sandboxes",
    indices = [Index(value = ["name"], unique = true)],
)
data class SandboxEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val linuxDistro: LinuxDistro = LinuxDistro.ALPINE,
    val storagePath: String,
    val status: SandboxStatus = SandboxStatus.CREATED,
    val activeSessionCount: Int = 0,
    val lastError: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
