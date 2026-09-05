package com.agon.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.agon.app.data.db.dao.AgentDao
import com.agon.app.data.db.dao.CommandExecutionDao
import com.agon.app.data.db.dao.CommandPolicyDao
import com.agon.app.data.db.dao.CredentialDao
import com.agon.app.data.db.dao.ProfileDao
import com.agon.app.data.db.dao.SandboxDao
import com.agon.app.data.db.dao.TerminalSessionDao
import com.agon.app.data.db.entity.AgentEntity
import com.agon.app.data.db.entity.CommandExecutionEntity
import com.agon.app.data.db.entity.CommandPolicyEntity
import com.agon.app.data.db.entity.CredentialEntity
import com.agon.app.data.db.entity.ProfileEntity
import com.agon.app.data.db.entity.SandboxEntity
import com.agon.app.data.db.entity.TerminalSessionEntity
import com.agon.app.data.model.CommandClassification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Database(
    entities = [
        ProfileEntity::class,
        AgentEntity::class,
        SandboxEntity::class,
        TerminalSessionEntity::class,
        CommandExecutionEntity::class,
        CommandPolicyEntity::class,
        CredentialEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class MusGoDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun agentDao(): AgentDao
    abstract fun sandboxDao(): SandboxDao
    abstract fun terminalSessionDao(): TerminalSessionDao
    abstract fun commandExecutionDao(): CommandExecutionDao
    abstract fun commandPolicyDao(): CommandPolicyDao
    abstract fun credentialDao(): CredentialDao

    companion object {
        private const val DB_NAME = "musgo_os.db"

        @Volatile
        private var instance: MusGoDatabase? = null

        fun getInstance(context: Context): MusGoDatabase {
            return instance ?: synchronized(this) {
                instance ?: build(context.applicationContext).also { instance = it }
            }
        }

        private fun build(context: Context): MusGoDatabase {
            return Room.databaseBuilder(context, MusGoDatabase::class.java, DB_NAME)
                .setQueryExecutor(Executors.newFixedThreadPool(4))
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default command policies after schema creation
                        CoroutineScope(Dispatchers.IO).launch {
                            getInstance(context).commandPolicyDao().insertAll(defaultPolicies())
                        }
                    }
                })
                .build()
        }

        fun defaultPolicies(): List<CommandPolicyEntity> = listOf(
            CommandPolicyEntity(
                commandPattern = "^\\s*(ls|pwd|echo|cat|head|tail|wc|date|whoami|uname|id|env|printenv|which|type|file|stat|df|du|free|uptime)(\\s|$).*",
                classification = CommandClassification.SAFE,
                description = "Read-only / informational shell builtins and utilities",
            ),
            CommandPolicyEntity(
                commandPattern = "^\\s*(cd|mkdir|touch|cp|mv|chmod|chown|ln|tar|gzip|gunzip|zip|unzip|find|grep|sed|awk|sort|uniq|diff|tee)(\\s|$).*",
                classification = CommandClassification.REVIEW_REQUIRED,
                description = "Filesystem mutation or bulk text processing",
            ),
            CommandPolicyEntity(
                commandPattern = "^\\s*(rm|rmdir|dd|mkfs|fdisk|parted|shred|truncate)(\\s|$).*",
                classification = CommandClassification.DANGEROUS,
                description = "Destructive filesystem operations",
            ),
            CommandPolicyEntity(
                commandPattern = "rm\\s+(-[a-zA-Z]*r[a-zA-Z]*f|-rf|-fr)\\s+[/~]",
                classification = CommandClassification.BLOCKED,
                description = "Recursive force-delete of root or home",
            ),
            CommandPolicyEntity(
                commandPattern = "^\\s*(reboot|shutdown|poweroff|halt|init\\s+[06]|systemctl\\s+(poweroff|reboot|halt))(\\s|$).*",
                classification = CommandClassification.BLOCKED,
                description = "System power control",
            ),
            CommandPolicyEntity(
                commandPattern = "^\\s*(curl|wget|nc|ncat|netcat|ssh|scp|rsync|ftp|telnet)(\\s|$).*",
                classification = CommandClassification.REVIEW_REQUIRED,
                description = "Network egress utilities",
            ),
            CommandPolicyEntity(
                commandPattern = "^\\s*(su|sudo|doas|passwd|useradd|userdel|usermod|visudo)(\\s|$).*",
                classification = CommandClassification.DANGEROUS,
                description = "Privilege escalation / user management",
            ),
            CommandPolicyEntity(
                commandPattern = "^\\s*(kill|killall|pkill|nice|renice|nohup|timeout)(\\s|$).*",
                classification = CommandClassification.REVIEW_REQUIRED,
                description = "Process control",
            ),
            CommandPolicyEntity(
                commandPattern = ":\\(\\)\\s*\\{\\s*:\\s*\\|\\s*:\\s*&\\s*\\}\\s*;\\s*:",
                classification = CommandClassification.BLOCKED,
                description = "Fork bomb pattern",
            ),
            CommandPolicyEntity(
                commandPattern = "^\\s*(python|python3|perl|ruby|node|php|lua)(\\s|$).*",
                classification = CommandClassification.REVIEW_REQUIRED,
                description = "Script interpreter execution",
            ),
        )
    }
}
