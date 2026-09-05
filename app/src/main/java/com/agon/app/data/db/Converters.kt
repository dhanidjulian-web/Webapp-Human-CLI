package com.agon.app.data.db

import androidx.room.TypeConverter
import com.agon.app.data.model.CommandClassification
import com.agon.app.data.model.CommandStatus
import com.agon.app.data.model.CredentialType
import com.agon.app.data.model.LinuxDistro
import com.agon.app.data.model.ModelMode
import com.agon.app.data.model.SandboxStatus
import com.agon.app.data.model.ShellType

class Converters {
    @TypeConverter fun fromSandboxStatus(v: SandboxStatus): String = v.name
    @TypeConverter fun toSandboxStatus(v: String): SandboxStatus = SandboxStatus.valueOf(v)

    @TypeConverter fun fromLinuxDistro(v: LinuxDistro): String = v.name
    @TypeConverter fun toLinuxDistro(v: String): LinuxDistro = LinuxDistro.valueOf(v)

    @TypeConverter fun fromShellType(v: ShellType): String = v.name
    @TypeConverter fun toShellType(v: String): ShellType = ShellType.valueOf(v)

    @TypeConverter fun fromCommandStatus(v: CommandStatus): String = v.name
    @TypeConverter fun toCommandStatus(v: String): CommandStatus = CommandStatus.valueOf(v)

    @TypeConverter fun fromCommandClassification(v: CommandClassification): String = v.name
    @TypeConverter fun toCommandClassification(v: String): CommandClassification = CommandClassification.valueOf(v)

    @TypeConverter fun fromCredentialType(v: CredentialType): String = v.name
    @TypeConverter fun toCredentialType(v: String): CredentialType = CredentialType.valueOf(v)

    @TypeConverter fun fromModelMode(v: ModelMode): String = v.name
    @TypeConverter fun toModelMode(v: String): ModelMode = ModelMode.valueOf(v)
}
