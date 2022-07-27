package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName

data class VersionData(
    @SerializedName("major")
    val major: Int,
    @SerializedName("minor")
    val minor: Int,
    @SerializedName("build")
    val build: Int
) {

    companion object {

        fun createFromString(string: String): VersionData {
            val arr = string.split(".")
            return VersionData(arr[0].toInt(), arr[1].toInt(), arr[2].toInt())
        }
    }

    override fun toString(): String {
        return "$major.$minor.$build"
    }

    fun biggerThan(otherVersionData: VersionData): Boolean {
        if (major < otherVersionData.major || minor < otherVersionData.minor) {
            return false
        }
        return major > otherVersionData.major || minor > otherVersionData.minor || build > otherVersionData.build
    }

    fun biggerThan(string: String): Boolean =
        biggerThan(createFromString(string))
}
