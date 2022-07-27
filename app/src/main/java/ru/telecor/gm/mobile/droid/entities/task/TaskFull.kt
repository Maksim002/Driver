package ru.telecor.gm.mobile.droid.entities.task


import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.StatusType
import java.text.SimpleDateFormat

data class TaskFull(
    @SerializedName("id")
    val id: Int,
    @SerializedName("planTimeStart")
    var planTimeStart: String,
    @SerializedName("changeTime")
    var changeTime: String,
    @SerializedName("standId")
    val standId: String?,
    @SerializedName("visitPointId")
    val visitPointId: String?,
    @SerializedName("taskItems")
    val taskItems: List<TaskItem>,
    @SerializedName("comment")
    val comment: String,
    @SerializedName("shippingByPiece")
    val shippingByPiece: Boolean,
    @SerializedName("containerAction")
    val containerAction: ContainerAction?,
    @SerializedName("preferredTimeStart")
    var preferredTimeStart: String ,
    @SerializedName("preferredTimeEnd")
    var preferredTimeEnd: String,
    @SerializedName("priority")
    val priority: Priority?
)
