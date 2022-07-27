package ru.telecor.gm.mobile.droid.entities.task

import com.google.gson.annotations.SerializedName

data class Simple(
    @SerializedName("id")
    val id : Int,
    @SerializedName("type")
    val type: SimpleType,
    @SerializedName("name")
    val name:  String?,
    @SerializedName("planTimeStart")
    val planTimeStart: String?,
    @SerializedName("changeTime")
    val changeTime: String?
)
enum class SimpleType{
    VP,
    SO
}

