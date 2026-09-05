package com.agon.app.security

import com.agon.app.data.model.CredentialType
import com.agon.app.data.model.CredentialValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Real network / format validation for stored credentials.
 * Never logs plaintext secrets.
 */
class CredentialValidator(
    private val httpClient: OkHttpClient = defaultClient(),
) {
    companion object {
        private fun defaultClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()

        private val PEM_PRIVATE_KEY = Pattern.compile(
            "-----BEGIN (?:RSA |OPENSSH |EC |DSA )?PRIVATE KEY-----[\\s\\S]+-----END (?:RSA |OPENSSH |EC |DSA )?PRIVATE KEY-----",
            Pattern.MULTILINE,
        )
    }

    suspend fun validate(type: CredentialType, plaintext: String): CredentialValidationResult =
        withContext(Dispatchers.IO) {
            val value = plaintext.trim()
            if (value.isEmpty()) {
                return@withContext CredentialValidationResult(false, "Empty credential")
            }
            try {
                when (type) {
                    CredentialType.GITHUB_PAT -> validateGitHub(value)
                    CredentialType.OPENROUTER_API_KEY -> validateOpenRouter(value)
                    CredentialType.GEMINI_API_KEY -> validateGemini(value)
                    CredentialType.GROQ_API_KEY -> validateGroq(value)
                    CredentialType.OPENAI_API_KEY -> validateOpenAI(value)
                    CredentialType.ANTHROPIC_API_KEY -> validateAnthropic(value)
                    CredentialType.SSH_PRIVATE_KEY -> validateSshKey(value)
                    CredentialType.SSH_PASSWORD -> validatePasswordFormat(value)
                    CredentialType.GENERIC_API_KEY -> validateGenericKey(value)
                }
            } catch (e: Exception) {
                CredentialValidationResult(
                    success = false,
                    message = "Validation error: ${e.javaClass.simpleName}: ${e.message?.take(120)}",
                )
            }
        }

    private fun validateGitHub(token: String): CredentialValidationResult {
        val request = Request.Builder()
            .url("https://api.github.com/user")
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("User-Agent", "MusGo-OS-CredentialValidator")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            return if (response.isSuccessful) {
                val json = runCatching { JSONObject(body) }.getOrNull()
                val login = json?.optString("login").orEmpty()
                val id = json?.optLong("id")?.toString().orEmpty()
                CredentialValidationResult(
                    success = true,
                    message = "GitHub OK — authenticated as ${login.ifBlank { "user" }}",
                    metadata = mapOf("login" to login, "id" to id),
                )
            } else {
                CredentialValidationResult(
                    success = false,
                    message = "GitHub rejected token (HTTP ${response.code})",
                )
            }
        }
    }

    private fun validateOpenRouter(key: String): CredentialValidationResult {
        val request = Request.Builder()
            .url("https://openrouter.ai/api/v1/models")
            .header("Authorization", "Bearer $key")
            .header("User-Agent", "MusGo-OS-CredentialValidator")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                CredentialValidationResult(true, "OpenRouter OK — models endpoint reachable")
            } else {
                CredentialValidationResult(false, "OpenRouter rejected key (HTTP ${response.code})")
            }
        }
    }

    private fun validateGemini(key: String): CredentialValidationResult {
        val url =
            "https://generativelanguage.googleapis.com/v1/models?key=${java.net.URLEncoder.encode(key, "UTF-8")}"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "MusGo-OS-CredentialValidator")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                CredentialValidationResult(true, "Gemini OK — models list retrieved")
            } else {
                CredentialValidationResult(false, "Gemini rejected key (HTTP ${response.code})")
            }
        }
    }

    private fun validateGroq(key: String): CredentialValidationResult {
        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/models")
            .header("Authorization", "Bearer $key")
            .header("User-Agent", "MusGo-OS-CredentialValidator")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                CredentialValidationResult(true, "Groq OK — models endpoint reachable")
            } else {
                CredentialValidationResult(false, "Groq rejected key (HTTP ${response.code})")
            }
        }
    }

    private fun validateOpenAI(key: String): CredentialValidationResult {
        val request = Request.Builder()
            .url("https://api.openai.com/v1/models")
            .header("Authorization", "Bearer $key")
            .header("User-Agent", "MusGo-OS-CredentialValidator")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            return if (response.isSuccessful) {
                CredentialValidationResult(true, "OpenAI OK — models endpoint reachable")
            } else {
                CredentialValidationResult(false, "OpenAI rejected key (HTTP ${response.code})")
            }
        }
    }

    private fun validateAnthropic(key: String): CredentialValidationResult {
        // Anthropic has no simple list endpoint without a paid call;
        // we validate format + a lightweight messages probe is avoided to prevent cost.
        // Instead: hit /v1/models if available, else format check.
        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/models")
            .header("x-api-key", key)
            .header("anthropic-version", "2023-06-01")
            .header("User-Agent", "MusGo-OS-CredentialValidator")
            .get()
            .build()

        httpClient.newCall(request).execute().use { response ->
            return when {
                response.isSuccessful ->
                    CredentialValidationResult(true, "Anthropic OK — models endpoint reachable")
                response.code == 401 || response.code == 403 ->
                    CredentialValidationResult(false, "Anthropic rejected key (HTTP ${response.code})")
                response.code == 404 -> {
                    // Endpoint may not exist; fall back to format validation
                    if (key.startsWith("sk-ant-") && key.length > 20) {
                        CredentialValidationResult(
                            true,
                            "Anthropic key format valid (endpoint ${response.code}; live chat not probed)",
                        )
                    } else {
                        CredentialValidationResult(false, "Anthropic key format invalid")
                    }
                }
                else -> CredentialValidationResult(
                    false,
                    "Anthropic unexpected response (HTTP ${response.code})",
                )
            }
        }
    }

    private fun validateSshKey(pem: String): CredentialValidationResult {
        if (!PEM_PRIVATE_KEY.matcher(pem).find()) {
            return CredentialValidationResult(
                false,
                "Not a valid PEM/OpenSSH private key block",
            )
        }
        if (pem.contains("ENCRYPTED") && !pem.contains("Proc-Type")) {
            // still acceptable — encrypted keys are valid
        }
        val lineCount = pem.lines().count { it.isNotBlank() }
        return CredentialValidationResult(
            success = true,
            message = "SSH private key structure valid ($lineCount lines)",
            metadata = mapOf("lines" to lineCount.toString()),
        )
    }

    private fun validatePasswordFormat(password: String): CredentialValidationResult {
        return if (password.length >= 4) {
            CredentialValidationResult(true, "Password accepted (length ${password.length}, stored encrypted)")
        } else {
            CredentialValidationResult(false, "Password too short (min 4)")
        }
    }

    private fun validateGenericKey(key: String): CredentialValidationResult {
        return if (key.length >= 8) {
            CredentialValidationResult(true, "API key format accepted (length ${key.length})")
        } else {
            CredentialValidationResult(false, "API key too short (min 8)")
        }
    }
}
