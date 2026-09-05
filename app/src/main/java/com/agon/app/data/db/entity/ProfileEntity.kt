package com.agon.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.agon.app.data.model.ModelMode

@Entity(
    tableName = "profiles",
    indices = [Index(value = ["name"], unique = true)],
)
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val activeStatus: Boolean = true,
    val selectedModelMode: ModelMode = ModelMode.CLOUD,
    val selectedModelId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

/** Hard limit: max agents allowed per profile (F004). */
const val MAX_AGENTS_PER_PROFILE = 30
