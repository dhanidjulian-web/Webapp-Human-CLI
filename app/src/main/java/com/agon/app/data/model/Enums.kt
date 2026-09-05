package com.agon.app.data.model

enum class SandboxStatus {
    CREATED,
    RUNNING,
    STOPPED,
    ERROR,
}

enum class LinuxDistro(val displayName: String, val shellDefault: String) {
    ALPINE("Alpine Linux", "sh"),
    DEBIAN("Debian", "bash"),
    UBUNTU("Ubuntu", "bash"),
}

enum class ShellType(val binary: String) {
    SH("sh"),
    BASH("bash"),
    ZSH("zsh"),
}

enum class CommandStatus {
    QUEUED,
    RUNNING,
    SUCCESS,
    FAILED,
}

enum class CommandClassification {
    SAFE,
    REVIEW_REQUIRED,
    DANGEROUS,
    BLOCKED,
}

enum class CredentialType(val displayName: String, val prefixHint: String) {
    GITHUB_PAT("GitHub PAT", "ghp_"),
    OPENROUTER_API_KEY("OpenRouter API Key", "sk-or-"),
    GEMINI_API_KEY("Gemini API Key", "AIza"),
    GROQ_API_KEY("Groq API Key", "gsk_"),
    OPENAI_API_KEY("OpenAI API Key", "sk-"),
    ANTHROPIC_API_KEY("Anthropic API Key", "sk-ant-"),
    SSH_PRIVATE_KEY("SSH Private Key", "-----BEGIN"),
    SSH_PASSWORD("SSH / VPS Password", ""),
    GENERIC_API_KEY("Generic API Key", ""),
}

enum class ModelMode {
    LOCAL,
    CLOUD,
    HYBRID,
}
