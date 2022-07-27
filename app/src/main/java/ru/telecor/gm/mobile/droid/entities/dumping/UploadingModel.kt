package ru.telecor.gm.mobile.droid.entities.dumping

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import ru.telecor.gm.mobile.droid.entities.dumping.GarbageTypeSecondModel
import ru.telecor.gm.mobile.droid.entities.dumping.VisitPointSecondModel

data class UploadingModel (
    @SerializedName("arriveTime")
    @Expose
    var arriveTime: String? = null,

    @SerializedName("departureTime")
    @Expose
    var departureTime: String? = null,

    @SerializedName("driver")
    @Expose
    var driver: String? = null,

    @SerializedName("garbageType")
    @Expose
    var garbageType: GarbageTypeSecondModel? = null,

    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("netWeight")
    @Expose
    var netWeight: Int? = null,

    @SerializedName("number")
    @Expose
    var number: Int? = null,

    @SerializedName("primaryWeight")
    @Expose
    var primaryWeight: Int? = null,

    @SerializedName("secondaryWeight")
    @Expose
    var secondaryWeight: Int? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("vehicle")
    @Expose
    var vehicle: String? = null,

    @SerializedName("visitPoint")
    @Expose
    var visitPoint: VisitPointSecondModel? = null,

    @SerializedName("weigherAction")
    @Expose
    var weigherAction: String? = null,

    @SerializedName("weighingTime")
    @Expose
    var weighingTime: String? = null
)