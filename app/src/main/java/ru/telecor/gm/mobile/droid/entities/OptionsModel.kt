package ru.telecor.gm.mobile.droid.model.repository.mod

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import ru.telecor.gm.mobile.droid.model.repository.mod.PhotoSize

class OptionsModel {
    @SerializedName("photoPeriodDelete")
    @Expose
    var photoPeriodDelete: Int? = null

    @SerializedName("photoSize")
    @Expose
    var photoSize: PhotoSize? = null

    @SerializedName("reloadDeep")
    @Expose
    var reloadDeep: Int? = null
}