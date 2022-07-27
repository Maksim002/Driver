package ru.telecor.gm.mobile.droid.entities.db

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.StatusType
import ru.telecor.gm.mobile.droid.entities.VisitPoint
import ru.telecor.gm.mobile.droid.entities.dumping.GetListCouponsModel
import ru.telecor.gm.mobile.droid.entities.stand.Stand
import ru.telecor.gm.mobile.droid.entities.task.ContainerAction
import ru.telecor.gm.mobile.droid.entities.task.Priority
import ru.telecor.gm.mobile.droid.entities.task.TaskItem
import ru.telecor.gm.mobile.droid.model.data.db.converters.DataConverters
import ru.telecor.gm.mobile.droid.model.data.db.converters.EnumConverters
import ru.telecor.gm.mobile.droid.model.data.db.converters.ListDataConverters

@Entity(tableName = "tasks")
data class TaskExtended(

    @Expose
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("planTimeStart")
    val planTimeStart: Long,

    @Expose
    @SerializedName("changeTime")
    val changeTime: Long,

    @Expose
    @SerializedName("stand")
    @TypeConverters(DataConverters::class)
    val stand: Stand?,

    @Expose
    @SerializedName("visitPoint")
    @TypeConverters(DataConverters::class)
    val visitPoint: VisitPoint?,

    @Expose
    @SerializedName("taskItems")
    @TypeConverters(ListDataConverters::class)
    val taskItems: List<TaskItem>,

    @Expose
    @SerializedName("statusType")
    @TypeConverters(EnumConverters::class)
    var statusType: StatusType,

    @Expose
    @SerializedName("comment")
    val comment: String?,

    @Expose
    @SerializedName("contactPhone")
    val contactPhone: String? = null,

    @Expose
    @SerializedName("shippingByPiece")
    val shippingByPiece: Boolean,

    @Expose
    @SerializedName("containerAction")
    @TypeConverters(DataConverters::class)
    val containerAction: ContainerAction,

    @Expose
    @SerializedName("preferredTimeStart")
    val preferredTimeStart: Long,

    @Expose
    @SerializedName("preferredTimeEnd")
    val preferredTimeEnd: Long,

    @Expose
    @SerializedName("priority")
    @TypeConverters(DataConverters::class)
    val priority: Priority? = null,

    @Expose
    @SerializedName("isCurrent")
    var isCurrent: Boolean = false,

    @Expose
    @SerializedName("order")
    var order: Double = 0.0,

    )
