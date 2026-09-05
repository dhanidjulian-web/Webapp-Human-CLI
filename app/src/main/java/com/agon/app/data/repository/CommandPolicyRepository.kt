package com.agon.app.data.repository

import com.agon.app.data.db.dao.CommandPolicyDao
import com.agon.app.data.db.entity.CommandPolicyEntity
import com.agon.app.data.model.CommandClassification
import kotlinx.coroutines.flow.Flow

class CommandPolicyRepository(
    private val dao: CommandPolicyDao,
) {
    fun observeAll(): Flow<List<CommandPolicyEntity>> = dao.observeAll()
    fun observeEnabled(): Flow<List<CommandPolicyEntity>> = dao.observeEnabled()

    suspend fun add(
        pattern: String,
        classification: CommandClassification,
        description: String,
    ): Long {
        require(pattern.isNotBlank()) { "Pattern required" }
        // Validate regex compiles
        Regex(pattern)
        return dao.insert(
            CommandPolicyEntity(
                commandPattern = pattern,
                classification = classification,
                description = description,
            ),
        )
    }

    suspend fun setEnabled(entity: CommandPolicyEntity, enabled: Boolean) {
        dao.update(entity.copy(enabled = enabled))
    }

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun ensureSeeded(defaults: List<CommandPolicyEntity>) {
        if (dao.count() == 0) {
            dao.insertAll(defaults)
        }
    }
}
