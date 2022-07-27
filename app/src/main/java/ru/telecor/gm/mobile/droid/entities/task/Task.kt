package ru.telecor.gm.mobile.droid.entities.task


import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.StatusType

data class Task(
    @SerializedName("id")
    val id: Int,
    @SerializedName("planTimeStart")
    val planTimeStart: Long,
    @SerializedName("changeTime")
    val changeTime: Long,
    @SerializedName("standId")
    val standId: String?,
    @SerializedName("visitPointId")
    val visitPointId: String?,
    @SerializedName("taskItems")
    val taskItems: List<TaskItem>,
    @SerializedName("statusType")
    var statusType: StatusType,
    @SerializedName("comment")
    val comment: String,
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
    val priority: Priority?
)