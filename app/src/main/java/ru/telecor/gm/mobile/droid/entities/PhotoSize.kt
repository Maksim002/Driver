package ru.telecor.gm.mobile.droid.model.repository.mod

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class PhotoSize {
    @SerializedName("height")
    @Expose
    var height: Int? = null

    @SerializedName("width")
    @Expose
    var width: Int? = null
}