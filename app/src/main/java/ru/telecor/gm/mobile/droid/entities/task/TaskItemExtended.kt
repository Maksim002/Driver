package ru.telecor.gm.mobile.droid.entities.task

import com.google.gson.annotations.SerializedName
import ru.telecor.gm.mobile.droid.entities.ContainerType
import ru.telecor.gm.mobile.droid.entities.Customer

data class TaskItemExtended(
    @SerializedName("id")
    val id: Int,
    @SerializedName("routeTaskId")
    val routeTaskId: Int,
    @SerializedName("containerTypeId")
    val containerType: ContainerType,
    @SerializedName("planCount")
    val planCount: Int,
    @SerializedName("planVolume")
    val planVolume: Double,
    @SerializedName("statuses")
    val statuses: List<StatusTaskExtended>,
    @SerializedName("detourPoints")
    val detourPoints: List<Any>,
    @SerializedName("paymentOverflow")
    val paymentOverflow: Boolean,
    @SerializedName("customer")
    val customer: Customer,
    @SerializedName("contract")
    val contract: Contract
)
