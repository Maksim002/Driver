package ru.telecor.gm.mobile.droid.entities

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.extensions.emptyIfNull

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities
 *
 * Information about loaders (porters).
 *
 * Created by Artem Skopincev (aka sharpyx) 29.07.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class LoaderInfo(
    @SerializedName("employeeId")
    val employeeId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("middleName")
    val middleName: String,
    @SerializedName("photo")
    val photo: PhotoModel? = null
) {

    override fun toString(): String =
        "${lastName.emptyIfNull()} ${firstName.emptyIfNull()} ${middleName.emptyIfNull()}"

}
