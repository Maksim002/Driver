package ru.telecor.gm.mobile.droid.entities.stand


import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.ResidentialPropertiesKind

data class Stand(
    @SerializedName("id")
    val id: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("residentialPropertiesKind")
    val residentialPropertiesKind: ResidentialPropertiesKind,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("evacuationPointId")
    val evacuationPointId: String,
    @SerializedName("evacuationPoint")
    val evacuationPoint: EvacuationPoint,
    @SerializedName("containerGroups")
    val containerGroups: List<ContainerGroup>,
    @SerializedName("changeTime")
    val changeTime: Long,
    @SerializedName("districtId")
    val districtId: String,
    @SerializedName("geoJson")
    val geoJson: String?
)