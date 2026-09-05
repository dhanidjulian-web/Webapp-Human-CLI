package com.agon.app.data.repository

import com.agon.app.data.db.dao.CredentialDao
import com.agon.app.data.db.entity.CredentialEntity
import com.agon.app.data.model.CredentialType
import com.agon.app.data.model.CredentialValidationResult
import com.agon.app.data.model.RedactedCredential
import com.agon.app.security.CredentialValidator
import com.agon.app.security.SecureCredentialManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Credential vault repository.
 * UI only ever sees [RedactedCredential] — plaintext never crosses this boundary upward.
 */
class CredentialRepository(
    private val dao: CredentialDao,
    private val crypto: SecureCredentialManager,
    private val validator: CredentialValidator = CredentialValidator(),
) {

    fun observeRedacted(): Flow<List<RedactedCredential>> =
        dao.observeAll().map { list -> list.map { it.toRedacted() } }

    fun observeRedactedByType(type: CredentialType): Flow<List<RedactedCredential>> =
        dao.observeByType(type).map { list -> list.map { it.toRedacted() } }

    suspend fun store(type: CredentialType, label: String, plaintext: String): RedactedCredential {
        require(label.isNotBlank()) { "Label required" }
        require(plaintext.isNotBlank()) { "Secret required" }
        val encrypted = crypto.encrypt(plaintext, type)
        val now = System.currentTimeMillis()
        val id = dao.insert(
            CredentialEntity(
                type = type,
                label = label.trim(),
                ciphertextBase64 = encrypted.ciphertextBase64,
                ivBase64 = encrypted.ivBase64,
                redactedPreview = encrypted.redactedPreview,
                createdAt = now,
                updatedAt = now,
            ),
        )
        return dao.getById(id)!!.toRedacted()
    }

    suspend fun updateSecret(id: Long, plaintext: String): RedactedCredential {
        val existing = dao.getById(id) ?: throw IllegalArgumentException("Credential $id not found")
        val encrypted = crypto.encrypt(plaintext, existing.type)
        val updated = existing.copy(
            ciphertextBase64 = encrypted.ciphertextBase64,
            ivBase64 = encrypted.ivBase64,
            redactedPreview = encrypted.redactedPreview,
            updatedAt = System.currentTimeMillis(),
            isValid = null,
            validationMessage = null,
            lastValidatedAt = null,
        )
        dao.update(updated)
        return updated.toRedacted()
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    /**
     * Decrypt + live-validate. Returns result; updates DB validation flags.
     * Plaintext is scoped to this function only.
     */
    suspend fun validate(id: Long): CredentialValidationResult {
        val entity = dao.getById(id) ?: return CredentialValidationResult(false, "Not found")
        val plain = crypto.decrypt(entity.ciphertextBase64, entity.ivBase64)
        val result = validator.validate(entity.type, plain)
        dao.updateValidation(id, result.success, result.message)
        return result
    }

    /**
     * Decrypt for authorized backend use only (e.g. injecting into a process env).
     * Caller MUST NOT log or put into UI state.
     */
    suspend fun unlockPlaintext(id: Long): String {
        val entity = dao.getById(id) ?: throw IllegalArgumentException("Credential $id not found")
        return crypto.decrypt(entity.ciphertextBase64, entity.ivBase64)
    }

    suspend fun count(): Int = dao.count()

    private fun CredentialEntity.toRedacted() = RedactedCredential(
        id = id,
        type = type,
        label = label,
        redactedValue = redactedPreview,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastValidatedAt = lastValidatedAt,
        isValid = isValid,
        validationMessage = validationMessage,
    )
}
