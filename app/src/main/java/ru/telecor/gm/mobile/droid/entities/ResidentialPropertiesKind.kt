package ru.telecor.gm.mobile.droid.entities


import com.google.gson.annotations.SerializedName

data class ResidentialPropertiesKind(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)