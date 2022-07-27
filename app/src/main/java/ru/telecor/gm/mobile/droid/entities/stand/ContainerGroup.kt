package ru.telecor.gm.mobile.droid.entities.stand


import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.ContainerType
import ru.telecor.gm.mobile.droid.entities.GarbageType

data class ContainerGroup(
    @SerializedName("id")
    val id: String,
    @SerializedName("containerType")
    val containerType: ContainerType,
    @SerializedName("garbageType")
    val garbageType: GarbageType,
    @SerializedName("count")
    val count: Int
)