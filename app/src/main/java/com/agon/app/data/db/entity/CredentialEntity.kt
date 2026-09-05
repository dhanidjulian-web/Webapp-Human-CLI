package com.agon.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.agon.app.data.model.CredentialType

/**
 * Stores ONLY AES-256-GCM ciphertext + IV + auth tag metadata.
 * Plaintext secrets never touch this table.
 */
@Entity(
    tableName = "credentials",
    indices = [Index(value = ["type", "label"], unique = true)],
)
data class CredentialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: CredentialType,
    val label: String,
    /** Base64-encoded AES-GCM ciphertext (includes auth tag). */
    val ciphertextBase64: String,
    /** Base64-encoded 12-byte GCM IV. */
    val ivBase64: String,
    /** Redacted preview safe for UI/logs (e.g. ghp_••••xxxx). */
    val redactedPreview: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastValidatedAt: Long? = null,
    val isValid: Boolean? = null,
    val validationMessage: String? = null,
)
