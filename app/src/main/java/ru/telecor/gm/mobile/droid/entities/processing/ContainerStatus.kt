package ru.telecor.gm.mobile.droid.entities.processing

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.ContainerFailureReason

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.processing
 *
 *
 *
 * Created by Artem Skopincev (aka sharpyx) 17.09.2020
 * Copyright © 2020 TKOInform. All rights reserved.
 */
data class ContainerStatus(
    @SerializedName("id")
    val id: Long? = null,
    @SerializedName("containerFailureReason")
    val containerFailureReason: ContainerFailureReason?= null,
    @SerializedName("containerTypeId")
    val containerTypeId: String? = null,
    @SerializedName("contractId")
    val contractId: String? = null,
    @SerializedName("createTime")
    val createTime: Long? = null,
    @SerializedName("statusType")
    val statusType: ContainerStatusType? = null,
    @SerializedName("volumeAct")
    val volumeAct: Double? = null,
    @SerializedName("volumePercent")
    var volumePercent: Double? = null
)