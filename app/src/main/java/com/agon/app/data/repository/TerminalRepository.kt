package com.agon.app.data.repository

import com.agon.app.data.db.dao.CommandExecutionDao
import com.agon.app.data.db.dao.TerminalSessionDao
import com.agon.app.data.db.entity.CommandExecutionEntity
import com.agon.app.data.db.entity.TerminalSessionEntity
import kotlinx.coroutines.flow.Flow
import java.io.File

class TerminalRepository(
    private val sessionDao: TerminalSessionDao,
    private val executionDao: CommandExecutionDao,
) {
    fun observeSessions(sandboxId: Long): Flow<List<TerminalSessionEntity>> =
        sessionDao.observeBySandbox(sandboxId)

    fun observeActiveSessions(): Flow<List<TerminalSessionEntity>> =
        sessionDao.observeActive()

    fun observeSession(id: Long): Flow<TerminalSessionEntity?> =
        sessionDao.observeById(id)

    fun observeExecutions(sessionId: Long): Flow<List<CommandExecutionEntity>> =
        executionDao.observeBySessionAsc(sessionId)

    fun observeRecentExecutions(limit: Int = 50): Flow<List<CommandExecutionEntity>> =
        executionDao.observeRecent(limit)

    suspend fun getSession(id: Long): TerminalSessionEntity? = sessionDao.getById(id)

    suspend fun readLogFile(path: String?): String {
        if (path.isNullOrBlank()) return ""
        val f = File(path)
        if (!f.exists() || !f.isFile) return ""
        // Cap read size to 512 KB for UI safety
        val max = 512 * 1024L
        return if (f.length() <= max) {
            f.readText(Charsets.UTF_8)
        } else {
            f.inputStream().use { input ->
                val skip = f.length() - max
                input.skip(skip)
                "…[truncated]…\n" + input.bufferedReader(Charsets.UTF_8).readText()
            }
        }
    }
}
