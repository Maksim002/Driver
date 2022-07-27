package ru.telecor.gm.mobile.droid.entities.task


import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.Customer

data class TaskItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("routeTaskId")
    val routeTaskId: Int,
    @SerializedName("containerTypeId")
    val containerTypeId: String,
    @SerializedName("planCount")
    val planCount: Int,
    @SerializedName("planVolume")
    val planVolume: Double,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("volume")
    val volume: Double?,
    @SerializedName("statuses")
    val statuses: List<StatusTask>,
    @SerializedName("detourPoints")
    val detourPoints: List<Any>,
    @SerializedName("paymentOverflow")
    val paymentOverflow: Boolean,
    @SerializedName("customer")
    val customer: Customer,
    @SerializedName("contract")
    val contract: Contract,
    @SerializedName("comment")
    val comment: String ?,
    @SerializedName("serviceAction")
    val service: ServiceAction?,
    @SerializedName("rule")
    val rule: String?
)
