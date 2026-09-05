package com.agon.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agon.app.data.db.entity.CommandPolicyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandPolicyDao {
    @Query("SELECT * FROM command_policies WHERE enabled = 1 ORDER BY id ASC")
    fun observeEnabled(): Flow<List<CommandPolicyEntity>>

    @Query("SELECT * FROM command_policies ORDER BY classification ASC, id ASC")
    fun observeAll(): Flow<List<CommandPolicyEntity>>

    @Query("SELECT * FROM command_policies WHERE enabled = 1")
    suspend fun getEnabled(): List<CommandPolicyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CommandPolicyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CommandPolicyEntity): Long

    @Update
    suspend fun update(entity: CommandPolicyEntity)

    @Query("SELECT COUNT(*) FROM command_policies")
    suspend fun count(): Int

    @Query("DELETE FROM command_policies WHERE id = :id")
    suspend fun deleteById(id: Long)
}
