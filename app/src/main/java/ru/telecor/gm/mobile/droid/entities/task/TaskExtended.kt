package ru.telecor.gm.mobile.droid.entities.task

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.VisitPoint
import ru.telecor.gm.mobile.droid.entities.stand.Stand

/**
 * Project Truck Crew
 * Package ru.telecor.gm.mobile.droid.entities.task
 *
 * Creating inside the client. Needed for keeping Stand instance inside the task, so it can be
 * comfortably used for ui binding.
 *
 * Created by Emil Zamaldinov (aka piligrim) 19.08.2020
 * Copyright © 2020 TKO-Inform. All rights reserved.
 */
data class TaskExtended(
    @SerializedName("id")
    val id: Int,
    @SerializedName("planTimeStart")
    val planTimeStart: Long,
    @SerializedName("changeTime")
    val changeTime: Long,
    @SerializedName("stand")
    val stand: Stand?,
    @SerializedName("visitPoint")
    val visitPoint: VisitPoint?,
    @SerializedName("taskItems")
    val taskItems: List<TaskItem>,
    @SerializedName("statusType")
    var statusType: StatusType,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("contactPhone")
    val contactPhone: String?,
    @SerializedName("shippingByPiece")
    val shippingByPiece: Boolean,
    @SerializedName("containerAction")
    val containerAction: ContainerAction,
    @SerializedName("preferredTimeStart")
    val preferredTimeStart: Long,
    @SerializedName("preferredTimeEnd")
    val preferredTimeEnd: Long,
    @SerializedName("priority")
    val priority: Priority?,
    @SerializedName("isCurrent")
    var isCurrent: Boolean = false
)
