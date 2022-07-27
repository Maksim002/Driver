package ru.telecor.gm.mobile.droid.model

import ru.telecor.gm.mobile.droid.entities.GmServerInfo

data class BuildCon(
    var buildVersion: String = "aplha"
)

enum class BuildVersion(val localName: String, val serverName: String, val viewName: String) {
    ALPHA("alpha","droid-team-v4","ALPHA"),
    BETA("beta","beta","BETA");

    companion object {
        fun fromName(tag: String): BuildVersion {
            when (tag) {
                "alpha" -> return ALPHA
                "beta" -> return BETA
            }
            throw Exception("not found")
        }

        fun fromServerName(tag: String): BuildVersion {
            when (tag) {
                "droid-team-v4" -> return ALPHA
                "beta" -> return BETA
            }
            throw Exception("not found")
        }
    }
}