package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CommandModel (
    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("action")
    @Expose
    var action: String? = null
)