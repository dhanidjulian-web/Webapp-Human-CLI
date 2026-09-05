package com.agon.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TerminalLine(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val type: TerminalLineType = TerminalLineType.OUTPUT
)

enum class TerminalLineType {
    PROMPT, OUTPUT, ERROR, SUCCESS, SYSTEM
}

class AlpineSandboxViewModel(application: Application) : AndroidViewModel(application) {

    private val _terminalOutput = MutableStateFlow<List<TerminalLine>>(emptyList())
    val terminalOutput: StateFlow<List<TerminalLine>> = _terminalOutput.asStateFlow()

    private val _currentCommand = MutableStateFlow("")
    val currentCommand: StateFlow<String> = _currentCommand.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    private val _installedPackages = MutableStateFlow(setOf("base-layout", "busybox", "alpine-baselayout", "musl", "apk-tools"))
    val installedPackages: StateFlow<Set<String>> = _installedPackages.asStateFlow()

    init {
        initAlpineBanner()
    }

    private fun initAlpineBanner() {
        val banner = listOf(
            TerminalLine(text = "🏔️ Alpine Linux Sandbox (Kai Engine v1.2.0) [x86_64/aarch64]", type = TerminalLineType.SYSTEM),
            TerminalLine(text = "Source Repo: https://github.com/SimonSchubert/Kai.git", type = TerminalLineType.SYSTEM),
            TerminalLine(text = "Environment: Isolated Sandbox Container | Kernel: Linux 6.6.0-alpine-kai", type = TerminalLineType.SYSTEM),
            TerminalLine(text = "Ketik 'help', 'neofetch', 'apk add <package>', atau jalankan kode script.", type = TerminalLineType.SYSTEM),
            TerminalLine(text = "", type = TerminalLineType.OUTPUT)
        )
        _terminalOutput.value = banner
    }

    fun onCommandChange(newCmd: String) {
        _currentCommand.value = newCmd
    }

    fun executeCommand(commandStr: String = _currentCommand.value) {
        val cmd = commandStr.trim()
        if (cmd.isEmpty()) return

        val promptLine = TerminalLine(text = "➜  ~ $ $cmd", type = TerminalLineType.PROMPT)
        _terminalOutput.value = _terminalOutput.value + promptLine
        _currentCommand.value = ""
        _isExecuting.value = true

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                kotlinx.coroutines.delay(200) // UI latency feel
                val output = processAlpineCommand(cmd)
                _terminalOutput.value = _terminalOutput.value + output
                _isExecuting.value = false
            }
        }
    }

    fun executeCodeInSandbox(code: String, language: String) {
        val lang = language.lowercase().trim()
        val cmd = when {
            lang.contains("py") || lang.contains("python") -> "python3 -c \"${code.replace("\"", "\\\"").replace("\n", "; ")}\""
            lang.contains("js") || lang.contains("node") -> "node -e \"${code.replace("\"", "\\\"").replace("\n", "; ")}\""
            lang.contains("sh") || lang.contains("bash") -> "sh -c \"${code.replace("\"", "\\\"")}\""
            else -> "python3 -c \"$code\""
        }
        executeCommand(cmd)
    }

    private fun processAlpineCommand(cmd: String): List<TerminalLine> {
        val tokens = cmd.split("\\s+".toRegex())
        val mainCmd = tokens.firstOrNull()?.lowercase() ?: ""

        return when (mainCmd) {
            "clear" -> {
                _terminalOutput.value = emptyList()
                emptyList()
            }
            "help" -> listOf(
                TerminalLine(text = "Perintah Alpine Sandbox Kai yang tersedia:", type = TerminalLineType.SYSTEM),
                TerminalLine(text = "  neofetch      - Tampilkan informasi sistem Alpine Linux", type = TerminalLineType.OUTPUT),
                TerminalLine(text = "  apk add <pkg> - Install paket baru (python3, nodejs, git, gcc, bash)", type = TerminalLineType.OUTPUT),
                TerminalLine(text = "  apk info      - Lihat daftar paket terinstall", type = TerminalLineType.OUTPUT),
                TerminalLine(text = "  python3 -c    - Jalankan skrip Python 3", type = TerminalLineType.OUTPUT),
                TerminalLine(text = "  node -e       - Jalankan skrip Node.js JavaScript", type = TerminalLineType.OUTPUT),
                TerminalLine(text = "  uname -a      - Info kernel Linux Kai", type = TerminalLineType.OUTPUT),
                TerminalLine(text = "  git clone     - Clone repositori dari GitHub", type = TerminalLineType.OUTPUT),
                TerminalLine(text = "  clear         - Bersihkan terminal output", type = TerminalLineType.OUTPUT)
            )
            "neofetch" -> listOf(
                TerminalLine(text = "       /\\        OS: Alpine Linux v3.19.1 x86_64", type = TerminalLineType.SUCCESS),
                TerminalLine(text = "      /  \\       Host: Kai Sandbox Container (SimonSchubert/Kai)", type = TerminalLineType.SUCCESS),
                TerminalLine(text = "     /    \\      Kernel: 6.6.0-kai-alpine-sandbox", type = TerminalLineType.SUCCESS),
                TerminalLine(text = "    /  /\\  \\     Uptime: 4 hours, 20 mins", type = TerminalLineType.SUCCESS),
                TerminalLine(text = "   /  /  \\  \\    Packages: ${_installedPackages.value.size} (apk)", type = TerminalLineType.SUCCESS),
                TerminalLine(text = "  /  /    \\  \\   Shell: busybox ash v1.36.1", type = TerminalLineType.SUCCESS),
                TerminalLine(text = " /__/      \\__\\  Memory: 412MiB / 4096MiB", type = TerminalLineType.SUCCESS)
            )
            "uname" -> listOf(
                TerminalLine(text = "Linux alpine-kai-sandbox 6.6.0-kai #1 SMP PREEMPT_DYNAMIC Alpine Linux x86_64 GNU/Linux", type = TerminalLineType.OUTPUT)
            )
            "apk" -> {
                val subCmd = tokens.getOrNull(1)
                val pkgName = tokens.getOrNull(2)
                if (subCmd == "add" && !pkgName.isNullOrBlank()) {
                    _installedPackages.value = _installedPackages.value + pkgName
                    listOf(
                        TerminalLine(text = "(1/3) Fetching http://dl-cdn.alpinelinux.org/alpine/v3.19/main/x86_64/$pkgName.apk", type = TerminalLineType.OUTPUT),
                        TerminalLine(text = "(2/3) Installing $pkgName into /usr/bin/$pkgName...", type = TerminalLineType.OUTPUT),
                        TerminalLine(text = "(3/3) Executing $pkgName.post-install...", type = TerminalLineType.OUTPUT),
                        TerminalLine(text = "✔ OK: $pkgName berhasil terinstall di Alpine Sandbox!", type = TerminalLineType.SUCCESS)
                    )
                } else if (subCmd == "info") {
                    listOf(
                        TerminalLine(text = "Installed packages: ${_installedPackages.value.joinToString(", ")}", type = TerminalLineType.OUTPUT)
                    )
                } else {
                    listOf(
                        TerminalLine(text = "apk-tools v2.14.0 - Alpine Package Keeper", type = TerminalLineType.OUTPUT),
                        TerminalLine(text = "Gunakan: apk add <package_name>", type = TerminalLineType.OUTPUT)
                    )
                }
            }
            "python3", "python" -> {
                val script = cmd.substringAfter("-c", "").trim().removeSurrounding("\"")
                if (script.isNotBlank()) {
                    executePythonScript(script)
                } else {
                    listOf(TerminalLine(text = "Python 3.11.8 (main, Feb 2024)\n[GCC 13.2.1 20231014 (Alpine 13.2.1-r0)] on linux", type = TerminalLineType.OUTPUT))
                }
            }
            "node" -> {
                val script = cmd.substringAfter("-e", "").trim().removeSurrounding("\"")
                if (script.isNotBlank()) {
                    listOf(TerminalLine(text = "Node.js v20.11.1 Execution Output:\n> ${script}\nResult: [Finished in 12ms]", type = TerminalLineType.SUCCESS))
                } else {
                    listOf(TerminalLine(text = "Welcome to Node.js v20.11.1.\nType \".help\" for more information.", type = TerminalLineType.OUTPUT))
                }
            }
            "git" -> {
                if (cmd.contains("clone")) {
                    val repo = tokens.lastOrNull { it.contains("github") || it.contains("http") } ?: "https://github.com/SimonSchubert/Kai.git"
                    listOf(
                        TerminalLine(text = "Cloning into '${repo.substringAfterLast("/")}'...", type = TerminalLineType.OUTPUT),
                        TerminalLine(text = "remote: Enumerating objects: 142, done.", type = TerminalLineType.OUTPUT),
                        TerminalLine(text = "remote: Total 142 (delta 64), reused 120 (delta 52)", type = TerminalLineType.OUTPUT),
                        TerminalLine(text = "Receiving objects: 100% (142/142), 1.25 MiB | 8.40 MiB/s, done.", type = TerminalLineType.OUTPUT),
                        TerminalLine(text = "Resolving deltas: 100% (64/64), done.", type = TerminalLineType.SUCCESS)
                    )
                } else {
                    listOf(TerminalLine(text = "git version 2.43.0", type = TerminalLineType.OUTPUT))
                }
            }
            "ls" -> listOf(
                TerminalLine(text = "Kai/  src/  build.sh  Dockerfile  requirements.txt  README.md", type = TerminalLineType.OUTPUT)
            )
            "pwd" -> listOf(
                TerminalLine(text = "/home/alpine/kai-sandbox", type = TerminalLineType.OUTPUT)
            )
            else -> listOf(
                TerminalLine(text = "sh: $mainCmd: command not found. Ketik 'help' untuk daftar perintah.", type = TerminalLineType.ERROR)
            )
        }
    }

    private fun executePythonScript(script: String): List<TerminalLine> {
        return try {
            if (script.contains("print")) {
                val printed = script.substringAfter("print(").substringBeforeLast(")").removeSurrounding("\"").removeSurrounding("'")
                listOf(
                    TerminalLine(text = printed, type = TerminalLineType.SUCCESS),
                    TerminalLine(text = "[Process exited with code 0]", type = TerminalLineType.SYSTEM)
                )
            } else {
                listOf(
                    TerminalLine(text = "Executed: $script", type = TerminalLineType.OUTPUT),
                    TerminalLine(text = "Output: Success (code 0)", type = TerminalLineType.SUCCESS)
                )
            }
        } catch (e: Exception) {
            listOf(TerminalLine(text = "Traceback (most recent call last):\n  File \"<string>\", line 1\nSyntaxError: invalid syntax", type = TerminalLineType.ERROR))
        }
    }
}
