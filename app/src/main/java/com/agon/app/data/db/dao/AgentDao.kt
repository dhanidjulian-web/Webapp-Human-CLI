package com.agon.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agon.app.data.db.entity.AgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents WHERE profileId = :profileId ORDER BY name ASC")
    fun observeByProfile(profileId: Long): Flow<List<AgentEntity>>

    @Query("SELECT * FROM agents WHERE id = :id")
    suspend fun getById(id: Long): AgentEntity?

    @Query("SELECT COUNT(*) FROM agents WHERE profileId = :profileId")
    suspend fun countByProfile(profileId: Long): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: AgentEntity): Long

    @Update
    suspend fun update(entity: AgentEntity)

    @Delete
    suspend fun delete(entity: AgentEntity)

    @Query("DELETE FROM agents WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM agents ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<AgentEntity>>
}
