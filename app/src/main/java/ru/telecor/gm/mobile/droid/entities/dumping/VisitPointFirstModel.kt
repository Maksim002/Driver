package ru.telecor.gm.mobile.droid.entities.dumping

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class VisitPointFirstModel (
    @SerializedName("equipmentStatus")
    @Expose
    var equipmentStatus: String? = null,

    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("recyclingType")
    @Expose
    var recyclingType: String? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("weigherType")
    @Expose
    var weigherType: String? = null,

    @SerializedName("weighingDevice")
    @Expose
    var weighingDevice: Boolean? = null
)