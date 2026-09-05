package com.agon.app.data.repository

import com.agon.app.data.db.dao.AgentDao
import com.agon.app.data.db.dao.ProfileDao
import com.agon.app.data.db.entity.AgentEntity
import com.agon.app.data.db.entity.MAX_AGENTS_PER_PROFILE
import com.agon.app.data.db.entity.ProfileEntity
import com.agon.app.data.model.ModelMode
import kotlinx.coroutines.flow.Flow

class ProfileRepository(
    private val profileDao: ProfileDao,
    private val agentDao: AgentDao,
) {
    fun observeProfiles(): Flow<List<ProfileEntity>> = profileDao.observeAll()
    fun observeProfile(id: Long): Flow<ProfileEntity?> = profileDao.observeById(id)
    fun observeAgents(profileId: Long): Flow<List<AgentEntity>> = agentDao.observeByProfile(profileId)
    fun observeAllAgents(): Flow<List<AgentEntity>> = agentDao.observeAll()

    suspend fun createProfile(
        name: String,
        description: String = "",
        mode: ModelMode = ModelMode.CLOUD,
        modelId: String = "",
    ): ProfileEntity {
        require(name.isNotBlank()) { "Profile name required" }
        val id = profileDao.insert(
            ProfileEntity(
                name = name.trim(),
                description = description.trim(),
                selectedModelMode = mode,
                selectedModelId = modelId,
            ),
        )
        return profileDao.getById(id)!!
    }

    suspend fun updateProfile(entity: ProfileEntity) {
        profileDao.update(entity.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProfile(id: Long) {
        profileDao.deleteById(id)
    }

    suspend fun addAgent(
        profileId: Long,
        name: String,
        description: String = "",
        promptInstructions: String = "",
        modelId: String = "",
    ): AgentEntity {
        val count = agentDao.countByProfile(profileId)
        require(count < MAX_AGENTS_PER_PROFILE) {
            "Maximum $MAX_AGENTS_PER_PROFILE agents per profile reached"
        }
        require(name.isNotBlank()) { "Agent name required" }
        val id = agentDao.insert(
            AgentEntity(
                profileId = profileId,
                name = name.trim(),
                description = description.trim(),
                promptInstructions = promptInstructions,
                selectedModelId = modelId,
            ),
        )
        return agentDao.getById(id)!!
    }

    suspend fun updateAgent(entity: AgentEntity) {
        agentDao.update(entity.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteAgent(id: Long) {
        agentDao.deleteById(id)
    }

    suspend fun agentCount(profileId: Long): Int = agentDao.countByProfile(profileId)
}
