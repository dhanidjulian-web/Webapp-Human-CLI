package com.agon.app.data.repository

import com.agon.app.data.model.ProviderSpec

object ProviderRepository {

    val PROVIDERS: List<ProviderSpec> = listOf(
        ProviderSpec(
            id = "groq",
            name = "Groq LPU",
            tagline = "Inference ultra-cepat ~500-800 token/detik pada LPU Hardware",
            freeTierDetails = "Gratis 14,400 Request/Hari (30 RPM). Tanpa Kartu Kredit.",
            rateLimitInfo = "30 RPM, 14.4K RPD, 6K TPM",
            latencyMs = 120,
            contextWindow = "128K Token",
            supportsVision = true,
            supportsCode = true,
            speedScore = 750.0,
            codeScore = 96,
            availableModels = listOf(
                "llama-3.3-70b-versatile",
                "deepseek-r1-distill-llama-70b",
                "mixtral-8x7b-32768",
                "llama-3.1-8b-instant"
            ),
            defaultModel = "llama-3.3-70b-versatile",
            websiteUrl = "https://groq.com",
            apiKeyUrl = "https://console.groq.com/keys",
            category = "Gratis"
        ),
        ProviderSpec(
            id = "gemini",
            name = "Google Gemini AI",
            tagline = "Multimodal cerdas & konteks 1M-2M token paling luas",
            freeTierDetails = "Gratis 15 RPM, 1,500 RPD pada Gemini 1.5 Flash & 2.0 Flash.",
            rateLimitInfo = "15 RPM, 1.5K RPD, 1M TPM",
            latencyMs = 280,
            contextWindow = "1M - 2M Token",
            supportsVision = true,
            supportsCode = true,
            speedScore = 320.0,
            codeScore = 94,
            availableModels = listOf(
                "gemini-2.0-flash",
                "gemini-1.5-flash",
                "gemini-1.5-pro"
            ),
            defaultModel = "gemini-2.0-flash",
            websiteUrl = "https://ai.google.dev",
            apiKeyUrl = "https://aistudio.google.com/app/apikey",
            category = "Gratis"
        ),
        ProviderSpec(
            id = "deepseek",
            name = "DeepSeek AI",
            tagline = "Model Pemrograman & Reasoning No.1 Terhebat & Termurah",
            freeTierDetails = "Bonus Saldo Awal Gratis & Tarif Freemium Sangat Murah ($0.14/M token).",
            rateLimitInfo = "Unthrottled High Throughput",
            latencyMs = 350,
            contextWindow = "64K Token",
            supportsVision = false,
            supportsCode = true,
            speedScore = 240.0,
            codeScore = 99,
            availableModels = listOf(
                "deepseek-chat",
                "deepseek-coder",
                "deepseek-reasoner"
            ),
            defaultModel = "deepseek-chat",
            websiteUrl = "https://deepseek.com",
            apiKeyUrl = "https://platform.deepseek.com/api_keys",
            category = "Freemium"
        ),
        ProviderSpec(
            id = "openrouter",
            name = "OpenRouter (Free Hub)",
            tagline = "Akses ratusan LLM termasuk model 100% Gratis",
            freeTierDetails = "Model `:free` tanpa bayar (DeepSeek R1, Llama 3.3 70B Free, Qwen 2.5 Coder).",
            rateLimitInfo = "20 RPM untuk model gratis",
            latencyMs = 400,
            contextWindow = "128K Token",
            supportsVision = true,
            supportsCode = true,
            speedScore = 210.0,
            codeScore = 95,
            availableModels = listOf(
                "deepseek/deepseek-r1:free",
                "meta-llama/llama-3.3-70b-instruct:free",
                "qwen/qwen-2.5-coder-32b-instruct:free",
                "mistralai/mistral-7b-instruct:free"
            ),
            defaultModel = "deepseek/deepseek-r1:free",
            websiteUrl = "https://openrouter.ai",
            apiKeyUrl = "https://openrouter.ai/keys",
            category = "Gratis"
        ),
        ProviderSpec(
            id = "sambanova",
            name = "SambaNova Systems",
            tagline = "Rekord Kecepatan Tercepat Llama 3.3 70B & Qwen 2.5 72B",
            freeTierDetails = "Gratis API Key Developer Access tanpa limit ketat.",
            rateLimitInfo = "High Speed Tier",
            latencyMs = 110,
            contextWindow = "128K Token",
            supportsVision = false,
            supportsCode = true,
            speedScore = 950.0,
            codeScore = 96,
            availableModels = listOf(
                "Meta-Llama-3.3-70B-Instruct",
                "Qwen2.5-Coder-32B-Instruct",
                "Qwen2.5-72B-Instruct"
            ),
            defaultModel = "Meta-Llama-3.3-70B-Instruct",
            websiteUrl = "https://sambanova.ai",
            apiKeyUrl = "https://cloud.sambanova.ai/apis",
            category = "Gratis"
        ),
        ProviderSpec(
            id = "cerebras",
            name = "Cerebras Inference",
            tagline = "Wafer-Scale Engine ~1800+ Token/detik",
            freeTierDetails = "Tier Developer Gratis dengan kecepatan kilat.",
            rateLimitInfo = "30 RPM, 1M Token/Hari",
            latencyMs = 80,
            contextWindow = "8K - 128K Token",
            supportsVision = false,
            supportsCode = true,
            speedScore = 1800.0,
            codeScore = 92,
            availableModels = listOf(
                "llama3.3-70b",
                "llama3.1-8b"
            ),
            defaultModel = "llama3.3-70b",
            websiteUrl = "https://cerebras.ai",
            apiKeyUrl = "https://cloud.cerebras.ai/platform",
            category = "Gratis"
        ),
        ProviderSpec(
            id = "mistral",
            name = "Mistral AI / Codestral",
            tagline = "Spesialis Coding & Bahasa Pemrograman Eropa",
            freeTierDetails = "Kredit gratis La Plateforme untuk Codestral & Mistral Small.",
            rateLimitInfo = "10 RPM, Free Trial Tier",
            latencyMs = 300,
            contextWindow = "32K Token",
            supportsVision = true,
            supportsCode = true,
            speedScore = 260.0,
            codeScore = 97,
            availableModels = listOf(
                "codestral-latest",
                "mistral-small-latest",
                "mistral-large-latest"
            ),
            defaultModel = "codestral-latest",
            websiteUrl = "https://mistral.ai",
            apiKeyUrl = "https://console.mistral.ai/api-keys",
            category = "Freemium"
        ),
        ProviderSpec(
            id = "together",
            name = "Together AI",
            tagline = "Cloud Inference Terlengkap untuk Open Source Models",
            freeTierDetails = "Gratis $25 Kredit Pendaftaran awal.",
            rateLimitInfo = "60 RPM, 100K TPM",
            latencyMs = 220,
            contextWindow = "128K Token",
            supportsVision = true,
            supportsCode = true,
            speedScore = 380.0,
            codeScore = 93,
            availableModels = listOf(
                "meta-llama/Llama-3.3-70B-Instruct-Turbo",
                "Qwen/Qwen2.5-Coder-32B-Instruct",
                "deepseek-ai/DeepSeek-R1"
            ),
            defaultModel = "Qwen/Qwen2.5-Coder-32B-Instruct",
            websiteUrl = "https://together.ai",
            apiKeyUrl = "https://api.together.ai/settings/api-keys",
            category = "Freemium"
        ),
        ProviderSpec(
            id = "huggingface",
            name = "HuggingFace Serverless",
            tagline = "Inference Serverless Open Source Raksasa",
            freeTierDetails = "Gratis Inference API Token dengan rate limit komutatif.",
            rateLimitInfo = "Rate Limit Berdasar Load",
            latencyMs = 450,
            contextWindow = "32K Token",
            supportsVision = false,
            supportsCode = true,
            speedScore = 180.0,
            codeScore = 90,
            availableModels = listOf(
                "Qwen/Qwen2.5-Coder-32B-Instruct",
                "bigcode/starcoder2-15b",
                "codellama/CodeLlama-34b-Instruct-hf"
            ),
            defaultModel = "Qwen/Qwen2.5-Coder-32B-Instruct",
            websiteUrl = "https://huggingface.co",
            apiKeyUrl = "https://huggingface.co/settings/tokens",
            category = "Gratis"
        ),
        ProviderSpec(
            id = "local_gguf",
            name = "Local GGUF (Mount File)",
            tagline = "Eksekusi offline 100% di HP / Ollama Local Server",
            freeTierDetails = "100% Gratis Selamanya, Privasi Total, Tanpa Internet & Tanpa API Key.",
            rateLimitInfo = "Unlimited (Tergantung RAM & CPU Device)",
            latencyMs = 50,
            contextWindow = "4K - 128K (Bisa diatur)",
            supportsVision = false,
            supportsCode = true,
            speedScore = 85.0,
            codeScore = 91,
            availableModels = listOf(
                "local-mounted-gguf",
                "qwen2.5-coder-7b.gguf",
                "deepseek-r1-distill-qwen-7b.gguf",
                "llama-3.2-3b-instruct.gguf"
            ),
            defaultModel = "local-mounted-gguf",
            websiteUrl = "https://github.com/ggerganov/llama.cpp",
            apiKeyUrl = "",
            isBYOKSupported = false,
            isLocal = true,
            category = "Lokal"
        )
    )

    fun getProviderById(id: String): ProviderSpec? {
        return PROVIDERS.find { it.id == id }
    }
}
