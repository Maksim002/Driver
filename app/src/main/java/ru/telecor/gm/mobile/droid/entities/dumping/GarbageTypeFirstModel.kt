package ru.telecor.gm.mobile.droid.entities.dumping

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class GarbageTypeFirstModel (
    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null
)