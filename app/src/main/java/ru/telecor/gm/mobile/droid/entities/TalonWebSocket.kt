package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import ru.telecor.gm.mobile.droid.entities.CommandModel

class TalonWebSocket (
    @SerializedName("unitId")
    @Expose
    var unitId: String? = null,

    @SerializedName("id")
    @Expose
    var id: String? = null,

    @SerializedName("command")
    @Expose
    var command: CommandModel? = null
)
