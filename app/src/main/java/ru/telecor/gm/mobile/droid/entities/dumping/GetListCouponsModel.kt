package ru.telecor.gm.mobile.droid.entities.dumping

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.util.ArrayList

data class GetListCouponsModel (
    @SerializedName("current")
    @Expose
    var current: Boolean? = null,

    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("number")
    @Expose
    var number: Int? = null,

    @SerializedName("recycling")
    @Expose
    var recycling: RecyclingModel? = null,

    @SerializedName("unloading")
    @Expose
    var unloading: ArrayList<UnloadingModel>? = null,

    @SerializedName("uploading")
    @Expose
    var uploading: ArrayList<UploadingModel>? = null
)