package com.agon.app.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.agon.app.data.model.CredentialType
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Production-grade AES-256-GCM credential vault backed by Android Keystore.
 *
 * - Master key never leaves the hardware-backed Keystore (when available).
 * - Each encrypt() call uses a fresh 12-byte IV.
 * - Ciphertext = IV || ciphertext+tag is NOT how we store; IV and body are separate columns.
 * - Redaction is mandatory before any UI / log / AI prompt exposure.
 */
class SecureCredentialManager {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "musgo_os_credential_master_v1"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_BITS = 128
        private const val GCM_IV_BYTES = 12
        private const val AES_KEY_BITS = 256

        @Volatile
        private var instance: SecureCredentialManager? = null

        fun getInstance(): SecureCredentialManager {
            return instance ?: synchronized(this) {
                instance ?: SecureCredentialManager().also { instance = it }
            }
        }
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    init {
        ensureMasterKey()
    }

    private fun ensureMasterKey() {
        if (keyStore.containsAlias(KEY_ALIAS)) return

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE,
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(AES_KEY_BITS)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    private fun getMasterKey(): SecretKey {
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    data class EncryptedPayload(
        val ciphertextBase64: String,
        val ivBase64: String,
        val redactedPreview: String,
    )

    /**
     * Encrypt plaintext secret with AES-256-GCM.
     * Returns Base64 ciphertext + IV and a redacted preview safe for storage UI.
     */
    fun encrypt(plaintext: String, type: CredentialType): EncryptedPayload {
        require(plaintext.isNotBlank()) { "Credential value must not be blank" }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getMasterKey())
        val iv = cipher.iv
        require(iv.size == GCM_IV_BYTES) { "Unexpected IV length: ${iv.size}" }

        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        return EncryptedPayload(
            ciphertextBase64 = Base64.encodeToString(ciphertext, Base64.NO_WRAP),
            ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP),
            redactedPreview = redact(plaintext, type),
        )
    }

    /**
     * Decrypt a stored payload. Use ONLY inside trusted backend paths
     * (validation, authorized injection). Never pass result to UI state.
     */
    fun decrypt(ciphertextBase64: String, ivBase64: String): String {
        val ciphertext = Base64.decode(ciphertextBase64, Base64.NO_WRAP)
        val iv = Base64.decode(ivBase64, Base64.NO_WRAP)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, getMasterKey(), spec)
        val plain = cipher.doFinal(ciphertext)
        return String(plain, Charsets.UTF_8)
    }

    /**
     * Redact secrets for UI / logs / AI prompt context.
     * Examples: ghp_••••a1b2, sk-••••wxyz, AIza••••9f0e
     */
    fun redact(plaintext: String, type: CredentialType): String {
        val trimmed = plaintext.trim()
        if (trimmed.isEmpty()) return "••••"

        if (type == CredentialType.SSH_PRIVATE_KEY) {
            val lines = trimmed.lines().filter { it.isNotBlank() }
            val header = lines.firstOrNull()?.take(20) ?: "-----BEGIN"
            return "$header •••• [REDACTED PRIVATE KEY] ••••"
        }

        if (type == CredentialType.SSH_PASSWORD) {
            return "•".repeat(minOf(trimmed.length, 12).coerceAtLeast(8))
        }

        val prefix = when {
            trimmed.startsWith("ghp_") -> "ghp_"
            trimmed.startsWith("gho_") -> "gho_"
            trimmed.startsWith("github_pat_") -> "github_pat_"
            trimmed.startsWith("sk-or-") -> "sk-or-"
            trimmed.startsWith("sk-ant-") -> "sk-ant-"
            trimmed.startsWith("sk-") -> "sk-"
            trimmed.startsWith("gsk_") -> "gsk_"
            trimmed.startsWith("AIza") -> "AIza"
            type.prefixHint.isNotEmpty() && trimmed.startsWith(type.prefixHint) -> type.prefixHint
            else -> ""
        }

        val body = if (prefix.isNotEmpty()) trimmed.removePrefix(prefix) else trimmed
        val tail = if (body.length >= 4) body.takeLast(4) else body
        return "${prefix}••••$tail"
    }

    /** Redact any free-form string that might contain secrets (for logs). */
    fun redactInText(text: String, knownSecrets: List<String>): String {
        var result = text
        for (secret in knownSecrets) {
            if (secret.length < 8) continue
            result = result.replace(secret, "[REDACTED]")
        }
        return result
    }

    /** Pack IV + ciphertext into a single blob (optional helper for file storage). */
    fun pack(ciphertextBase64: String, ivBase64: String): ByteArray {
        val iv = Base64.decode(ivBase64, Base64.NO_WRAP)
        val ct = Base64.decode(ciphertextBase64, Base64.NO_WRAP)
        return ByteBuffer.allocate(4 + iv.size + ct.size)
            .putInt(iv.size)
            .put(iv)
            .put(ct)
            .array()
    }

    fun unpack(blob: ByteArray): Pair<String, String> {
        val buf = ByteBuffer.wrap(blob)
        val ivLen = buf.int
        val iv = ByteArray(ivLen)
        buf.get(iv)
        val ct = ByteArray(buf.remaining())
        buf.get(ct)
        return Base64.encodeToString(ct, Base64.NO_WRAP) to Base64.encodeToString(iv, Base64.NO_WRAP)
    }
}
